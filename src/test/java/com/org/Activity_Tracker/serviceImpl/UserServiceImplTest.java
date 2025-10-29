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

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
        session.invalidate();
    }

    @Test
    void createUser() {
        User user = new User("yo", "yo@gmail.com", "1234", Gender.MALE);
        userRepository.save(user);

        Optional<User> found = userRepository.findUserByUsernameAndPassword("yo", "1234");
        assertTrue(found.isPresent());
        assertEquals("yo", found.get().getUsername());
    }

    @Test
    void userLogin() {
        userRepository.save(new User("yom", "yom@gmail.com", "1234", Gender.MALE));

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
        userRepository.save(new User("yom", "yom@gmail.com", "1234", Gender.MALE));

        LoginRequest request = new LoginRequest();
        request.setUsername("yom");
        request.setPassword("1234");

        userService.userLogin(request, session);
        assertNotNull(session.getAttribute("currUser"));

        userService.userLogout(session);
        assertNull(session.getAttribute("currUser"));
    }
}
