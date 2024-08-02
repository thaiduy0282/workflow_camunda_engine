package com.qworks.camunda.config;

import com.qworks.camunda.exception.SystemException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        return switch (response.status()) {
            case 400 -> new SystemException("Bad request");
            case 404 -> new SystemException("Not found");
            default -> new SystemException("Unknown error");
        };
    }

}
