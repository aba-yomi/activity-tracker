package com.org.Activity_Tracker.services.serviceImpl;


import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.exceptions.UserNotFoundException;
import com.org.Activity_Tracker.pojos.LoginRequest;
import com.org.Activity_Tracker.pojos.RegistrationRequest;
import com.org.Activity_Tracker.repositories.UserRepository;
import com.org.Activity_Tracker.services.UserService;
import com.org.Activity_Tracker.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String createUser(RegistrationRequest request) {

        Boolean existsByEmail = userRepository.existsByEmail(request.getEmail().toLowerCase());
        Boolean existsByUsername = userRepository.existsByUsername(request.getUsername().toLowerCase());

        if(existsByUsername)
            return "Username taken!";

        if(existsByEmail)
            return "A User with this email already exist!";

        User user = User.builder()
                .username(request.getUsername().toLowerCase())
                .email(request.getEmail().toLowerCase())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .password(request.getPassword())
                .build();
        userRepository.save(user);
        return "Registration successful";
    }


    @Override
    public String userLogin(LoginRequest request, HttpSession session) {

        User user = userRepository.findUserByUsernameAndPassword(request.getUsername().toLowerCase(),
                        request.getPassword()).orElseThrow(()-> new UserNotFoundException("Wrong email or password"));

        session.setAttribute("currUser", user);
        return "Login successful";
    }

    @Override
    public String userLogout(HttpSession session){
        session.invalidate();
        return "You have been logged out, hope to see you again soon";
    }
}
