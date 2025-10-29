package com.org.Activity_Tracker.pojos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private Object message;
    private Boolean success;
    private HttpStatus status;
    private T data;

}
