package com.qworks.camunda.listener;

import com.qworks.camunda.client.ProcessHistoryStatus;
import com.qworks.camunda.client.UpdateProcessHistoryRequest;
import com.qworks.camunda.client.WorkflowApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component("taskExecutionListener")
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionListener implements ExecutionListener {

    private final WorkflowApiClient workflowApiClient;

    @Override
    public void notify(DelegateExecution execution) {
        String eventName = execution.getEventName();
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        String activityName = execution.getCurrentActivityName();
        String processDefinition = ((ExecutionEntity) execution).getProcessDefinition().getKey();

        log.info("Event: {} for task: {}", eventName, activityName);
        log.info("processDefinition: {}", processDefinition);

        switch (eventName) {
            case "start", "assign":
                workflowApiClient.updateProcessHistory(
                        UpdateProcessHistoryRequest.builder()
                                .status(ProcessHistoryStatus.RUNNING)
                                .startDate(Optional.of(new Date()))
                                .build(),
                        activityId
                );
                log.info("call client to create process history with process definition id {} successfully!", processDefinition);
                break;
            case "end", "complete":
                workflowApiClient.updateProcessHistory(
                        UpdateProcessHistoryRequest.builder()
                                .status(ProcessHistoryStatus.SUCCESS)
                                .endDate(Optional.of(new Date()))
                                .build(),
                        activityId
                );
                log.info("call client to update process history with process definition id {} successfully!", processDefinition);
                break;
            case "delete":
                break;
            case "take", "throw", "error":
                String errorDescription = Optional.ofNullable((String) execution.getVariable("errorMessage"))
                        .orElse("Unknown error");
                workflowApiClient.updateProcessHistory(
                        UpdateProcessHistoryRequest.builder()
                                .status(ProcessHistoryStatus.FAILED)
                                .endDate(Optional.of(new Date()))
                                .description(Optional.of(errorDescription))
                                .build(),
                        activityId
                );
                log.info("Call client to update process history with process definition id {} successfully!", processDefinition);
                break;
            default:
                break;
        }
    }

}