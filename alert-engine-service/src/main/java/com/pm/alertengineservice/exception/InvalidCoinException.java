package com.pm.alertengineservice.exception;

public class InvalidCoinException extends RuntimeException {
    public InvalidCoinException(String message) {
        super(message);
    }
}
