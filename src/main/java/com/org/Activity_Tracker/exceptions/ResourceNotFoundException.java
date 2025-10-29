package com.org.Activity_Tracker.exceptions;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException{

    private String debugMessage;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
    }


}
