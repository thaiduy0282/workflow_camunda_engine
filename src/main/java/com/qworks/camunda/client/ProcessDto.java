package com.qworks.camunda.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessDto {

    private String id;

    private String processDefinitionId;

    private String workflowId;

    private ProcessStatus status;

    private String currentActivity;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SS")
    private Date startDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SS")
    private Date lastModifiedDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SS")
    private Date endDate;

}
