package com.loyai.loyaiproject.exception;

public class ServiceUnAvailableException extends RuntimeException {
    public ServiceUnAvailableException() {
        super();
    }
    public ServiceUnAvailableException(String message) {
        super(message);
    }
}
