package com.qworks.camunda.client;

import com.qworks.camunda.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "workflows",
        url = "${spring.cloud.openfeign.client.config.workflows.url}",
        path = "/v1/process",
        configuration = FeignClientConfiguration.class
)
public interface WorkflowApiClient {

    @PatchMapping
    ResponseEntity<ProcessDto> updateProcessHistory(@RequestBody UpdateProcessRequest request);

}
