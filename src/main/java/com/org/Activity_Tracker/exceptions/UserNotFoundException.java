package com.org.Activity_Tracker.exceptions;

import lombok.Data;

@Data
public class UserNotFoundException extends NullPointerException{

    private String debugMsg;

    public UserNotFoundException(String s, String debugMsg) {
        super(s);
        this.debugMsg = debugMsg;
    }

    public UserNotFoundException(String s) {
        super(s);
    }
}
