package com.org.Activity_Tracker.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<GlobalErrorMessage> handleNotFoundException(ResourceNotFoundException ex){

        GlobalErrorMessage errorMsg = new GlobalErrorMessage();
        errorMsg.setMessage(ex.getMessage());
        errorMsg.setDebugMessage(ex.getDebugMessage());
        errorMsg.setStatus(HttpStatus.NOT_FOUND);

        return new ResponseEntity(errorMsg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<?> handleNullPointer(UserNotFoundException ex){

        GlobalErrorMessage errorMsg = new GlobalErrorMessage();
        errorMsg.setMessage(ex.getMessage());
        errorMsg.setDebugMessage("User not found");
        errorMsg.setStatus(HttpStatus.NOT_FOUND);

        return new ResponseEntity(errorMsg, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> customValidationExceptionHandler(MethodArgumentNotValidException ex){
        GlobalErrorMessage errorMessage = new GlobalErrorMessage(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage()
        );

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }



}



