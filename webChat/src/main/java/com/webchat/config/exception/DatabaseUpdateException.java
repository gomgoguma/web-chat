package com.webchat.config.exception;

public class DatabaseUpdateException extends RuntimeException{
    public DatabaseUpdateException(String message) {
        super(message);
    }
}
