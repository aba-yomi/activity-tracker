package com.org.Activity_Tracker.exceptions;

public class TypeMismatchException extends RuntimeException{

    private String debugMessage;

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
    }
}
