package com.org.Activity_Tracker.services.serviceImpl;


import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.exceptions.ResourceNotFoundException;
import com.org.Activity_Tracker.exceptions.UserNotFoundException;
import com.org.Activity_Tracker.pojos.LoginRequest;
import com.org.Activity_Tracker.pojos.RegistrationRequest;
import com.org.Activity_Tracker.repositories.UserRepository;
import com.org.Activity_Tracker.services.UserService;
import com.org.Activity_Tracker.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create/register a user. This will encode the password before saving.
     * Replace or adapt to your existing createUser/register method signature.
     */
    @Override
    public String createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceNotFoundException("Email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResourceNotFoundException("Username already in use");
        }

        // Encode the password before saving
        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        user.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(user);
        return "User created";
    }

    /**
     * Login: find user by username, then verify password with PasswordEncoder.matches
     */
    @Override
    public String userLogin(LoginRequest request, HttpSession session) {
        Optional<User> maybeUser = userRepository.findByUsername(request.getUsername());
        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException("Invalid username or password");
        }

        User user = maybeUser.get();
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new UserNotFoundException("Invalid username or password");
        }

        session.setAttribute("currUser", user);
        return "Logged in successfully";
    }

    @Override
    public String userLogout(HttpSession session){
        session.invalidate();
        return "You have been logged out, hope to see you again soon";
    }
}
