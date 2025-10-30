package com.org.Activity_Tracker.pojos;

public class AuthResponse {

    public String token;
    public String tokenType = "Bearer";


    public AuthResponse(String token) { this.token = token; }
}
