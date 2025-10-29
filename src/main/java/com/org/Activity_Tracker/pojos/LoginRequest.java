package com.org.Activity_Tracker.pojos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "username is required!")
    private String username;
    @NotBlank(message = "Password is required!")
    private String password;

}
