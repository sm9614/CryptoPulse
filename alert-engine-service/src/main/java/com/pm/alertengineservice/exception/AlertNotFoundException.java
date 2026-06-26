package com.pm.alertengineservice.exception;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(String message) {
        super(message);
    }
}
