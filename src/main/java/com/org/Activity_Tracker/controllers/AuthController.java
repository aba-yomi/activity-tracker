package com.org.Activity_Tracker.controllers;


import com.org.Activity_Tracker.pojos.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import com.org.Activity_Tracker.pojos.*;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Role;
import com.org.Activity_Tracker.repositories.UserRepository;
import com.org.Activity_Tracker.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public Object register(@RequestBody RegistrationRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) return "username-taken";
        if (userRepository.existsByEmail(req.getEmail())) return "email-taken";
        User u = new User(req.getUsername(), req.getEmail(), passwordEncoder.encode(req.getPassword()), Set.of(Role.ROLE_USER));
        userRepository.save(u);
        String token = jwtTokenProvider.generateToken(u.getUsername());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid-credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid-credentials");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
