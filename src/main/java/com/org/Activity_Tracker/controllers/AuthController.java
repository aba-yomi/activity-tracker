package com.org.Activity_Tracker.controllers;


import com.org.Activity_Tracker.services.UserService;
import com.org.Activity_Tracker.utils.ResponseManager;
import com.org.Activity_Tracker.pojos.ApiResponse;
import com.org.Activity_Tracker.pojos.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final HttpSession session;

    @PostMapping("/login")
    public ApiResponse<Object> userLogin(@Valid @RequestBody LoginRequest request){
        String response = userService.userLogin(request, session);
        return new ResponseManager().success(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ApiResponse<Object> userLogout(){
       String response = userService.userLogout(session);
       return new ResponseManager().success(response, HttpStatus.OK);
    }
}
