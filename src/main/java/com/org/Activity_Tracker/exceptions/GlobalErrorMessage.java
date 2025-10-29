package com.org.Activity_Tracker.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalErrorMessage {

    private HttpStatus status;
    private String message;
    private String debugMessage;
}
