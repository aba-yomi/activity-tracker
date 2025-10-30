package com.org.Activity_Tracker.serviceImpl;

import com.org.Activity_Tracker.services.UserService;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Gender;
import com.org.Activity_Tracker.pojos.LoginRequest;
import com.org.Activity_Tracker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession session;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
        session.invalidate();
    }

    @Test
    void createUser() {
        User user = new User("yo", "yo@gmail.com", "1234", Gender.MALE);
        userService.createUser(user);

        Optional<User> found = userRepository.findByUsername("yo");
        assertTrue(found.isPresent());
        assertEquals("yo", found.get().getUsername());

        String storedPassword = found.get().getPassword();
        assertNotNull(storedPassword);
        assertNotEquals("1234", storedPassword, "Password should be stored encoded, not plaintext");
        assertTrue(passwordEncoder.matches("1234", storedPassword), "Encoded password must match the raw password");
    }

    @Test
    void userLogin() {
        // create user via service (password encoded)
        userService.createUser(new User("yom", "yom@gmail.com", "1234", Gender.MALE));

        LoginRequest request = new LoginRequest();
        request.setUsername("yom");
        request.setPassword("1234");

        userService.userLogin(request, session);

        User user = (User) session.getAttribute("currUser");
        assertNotNull(user);
        assertEquals("yom", user.getUsername());
    }

    @Test
    void userLogout() {
        // create user via service (password encoded)
        userService.createUser(new User("yom", "yom@gmail.com", "1234", Gender.MALE));

        LoginRequest request = new LoginRequest();
        request.setUsername("yom");
        request.setPassword("1234");

        userService.userLogin(request, session);
        assertNotNull(session.getAttribute("currUser"));

        userService.userLogout(session);
        assertNull(session.getAttribute("currUser"));
    }
}
