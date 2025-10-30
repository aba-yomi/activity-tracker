package com.org.Activity_Tracker.serviceImpl;

import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Gender;
import com.org.Activity_Tracker.exceptions.UserNotFoundException;
import com.org.Activity_Tracker.pojos.LoginRequest;
import com.org.Activity_Tracker.repositories.UserRepository;
import com.org.Activity_Tracker.services.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void createUser() {
        User user = new User("yo", "yo@gmail.com", "1234", Gender.MALE);
        User savedUser = new User("yo", "yo@gmail.com", "encodedPassword", Gender.MALE);

        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findByUsername("yo")).thenReturn(Optional.of(savedUser));

        userService.createUser(user);

        verify(passwordEncoder).encode("1234");
        verify(userRepository).save(argThat(argument ->
                argument.getUsername().equals("yo") &&
                        argument.getPassword().equals("encodedPassword")
        ));

        Optional<User> found = userRepository.findByUsername("yo");
        assertTrue(found.isPresent());
        assertEquals("yo", found.get().getUsername());
        assertEquals("encodedPassword", found.get().getPassword());
    }

    @Test
    void userLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("yom");
        request.setPassword("1234");

        User existingUser = new User("yom", "yom@gmail.com", "encodedPassword", Gender.MALE);

        when(userRepository.findByUsername("yom")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("1234", "encodedPassword")).thenReturn(true);

        userService.userLogin(request, session);

        verify(userRepository).findByUsername("yom");
        verify(passwordEncoder).matches("1234", "encodedPassword");
        verify(session).setAttribute(eq("currUser"), any(User.class));
    }

    @Test
    void userLogin_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("1234");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.userLogin(request, session));

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void userLogin_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("yom");
        request.setPassword("wrongPassword");

        User existingUser = new User("yom", "yom@gmail.com", "encodedPassword", Gender.MALE);

        when(userRepository.findByUsername("yom")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.userLogin(request, session));

        verify(userRepository).findByUsername("yom");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void userLogout() {
        userService.userLogout(session);
        verify(session).invalidate();
    }

}
