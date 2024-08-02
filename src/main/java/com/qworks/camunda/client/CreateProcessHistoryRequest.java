package com.qworks.camunda.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record CreateProcessHistoryRequest(
        @NotBlank
        String processDefinitionId,

        @NotBlank
        String activityName,

        @NotBlank
        String activityId,

        @NotNull
        ProcessHistoryStatus status,

        @NotNull
        Date startedDate
) { }
