package com.qworks.camunda.listener;

import com.qworks.camunda.client.ProcessStatus;
import com.qworks.camunda.client.UpdateProcessRequest;
import com.qworks.camunda.client.WorkflowApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Component("taskExecutionListener")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionListener implements ExecutionListener {

    private final WorkflowApiClient workflowApiClient;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = getProcessInstanceId(execution);
        String activityId = execution.getCurrentActivityId();
        String activityName = execution.getCurrentActivityName();
        String processDefinition = ((ExecutionEntity) execution).getProcessDefinition().getKey();
        log.info("Event: {} for task: {}", eventName, activityName);
        log.info("processDefinition: {}", processDefinition);
        UpdateProcessRequest.UpdateProcessRequestBuilder updateProcessRequest = UpdateProcessRequest.builder()
                .processInstanceId(processInstanceId)
                .nodeId(activityId)
                .activityName(activityName)
                .startTime(Optional.of(new Date()))
                .endTime(Optional.of(new Date()));

        String redisKey = "process:variables:" + processInstanceId;
        switch (eventName) {
            case "start", "assign":
                updateProcessRequest.status(ProcessStatus.RUNNING);
                log.info("call client to create process history with process definition id {} successfully!", processDefinition);
                break;
            case "end", "complete":
                Boolean isFailed = (Boolean) redisTemplate.opsForHash().get(redisKey, "isFailed");
                if (BooleanUtils.isTrue(isFailed)) {
                    RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
                    String processInstanceId1 = execution.getProcessInstanceId();
                    runtimeService.deleteProcessInstance(processInstanceId1, "Process terminated by ExecutionListener");
                    updateProcessRequest.status(ProcessStatus.FAILED);
                    String errMsg = (String) redisTemplate.opsForHash().get(redisKey, "errMessage");
                    updateProcessRequest.note(StringUtils.hasLength(errMsg) ? errMsg.describeConstable() : "".describeConstable());
                } else {
                    updateProcessRequest.status(ProcessStatus.COMPLETED);
                    log.info("call client to update process history with process definition id {} successfully!", processDefinition);
                }
                break;
            case "delete":
                break;
            case "take", "throw", "error":
                String errorDescription = Optional.ofNullable((String) redisTemplate.opsForHash().get(redisKey, "errorMessage"))
                        .orElse("Unknown error");
                updateProcessRequest.status(ProcessStatus.FAILED);
                updateProcessRequest.note(Optional.of(errorDescription));
                log.info("ERROR - Call client to update process history with process definition id {} successfully!", processDefinition);
                break;
            default:
                break;
        }

        workflowApiClient.updateProcessHistory(updateProcessRequest.build());
    }

    private String getProcessInstanceId(DelegateExecution execution) {
        if (execution.getVariable("processInstanceId") != null) {
            return String.valueOf(execution.getVariable("processInstanceId"));
        }
        return execution.getParentActivityInstanceId();
    }
}