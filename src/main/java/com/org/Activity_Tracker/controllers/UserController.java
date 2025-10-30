package com.org.Activity_Tracker.controllers;


import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Gender;
import com.org.Activity_Tracker.services.UserService;
import com.org.Activity_Tracker.utils.ResponseManager;
import com.org.Activity_Tracker.pojos.ApiResponse;
import com.org.Activity_Tracker.pojos.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<Object> register_user(@Valid @RequestBody RegistrationRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setGender(Gender.valueOf(request.getGender()));
        String response = userService.createUser(user);
        return new ResponseManager().success(response, HttpStatus.CREATED);
    }

}
