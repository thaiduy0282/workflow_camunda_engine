package com.qworks.camunda.client;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Date;
import java.util.Optional;

@Builder
public record UpdateProcessHistoryRequest(

        @NotNull
        ProcessHistoryStatus status,

        Optional<Date> startDate,

        Optional<Date> endDate,

        Optional<String> description

) { }
