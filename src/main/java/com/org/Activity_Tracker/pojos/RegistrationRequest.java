package com.org.Activity_Tracker.pojos;


import lombok.*;

import javax.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 4, message = "Username must be at least 4 character long")
    private String username;

    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Please specify your gender")
    @Pattern(regexp = "MALE|FEMALE", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Invalid gender. Supported values: MALE, FEMALE")
    private String gender;


}
