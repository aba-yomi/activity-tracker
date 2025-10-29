package com.org.Activity_Tracker.utils;


import com.org.Activity_Tracker.pojos.ApiResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Data
@Service
public class ResponseManager {

    public ApiResponse<Object> success(Object data, HttpStatus status){

        return new ApiResponse<>("Request successful", true, status, data);
    }


    public ApiResponse<Object> failure(Object errorMsg, HttpStatus status){

        return new ApiResponse<>(errorMsg, false, null, status);
    }

}
