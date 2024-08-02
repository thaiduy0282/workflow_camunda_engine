package com.qworks.camunda.exception;

public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
