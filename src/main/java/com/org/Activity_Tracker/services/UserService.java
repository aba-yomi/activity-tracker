package com.org.Activity_Tracker.services;

import com.org.Activity_Tracker.pojos.LoginRequest;
import com.org.Activity_Tracker.pojos.RegistrationRequest;

import javax.servlet.http.HttpSession;

public interface UserService {

    String createUser(RegistrationRequest request);

    String userLogin(LoginRequest request, HttpSession session);

    String userLogout(HttpSession session);

}
