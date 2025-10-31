package com.org.Activity_Tracker.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.Activity_Tracker.controllers.UserController;
import com.org.Activity_Tracker.exceptions.GlobalExceptionHandler;
import com.org.Activity_Tracker.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUser_invalidGender_shouldReturnBadRequest() throws Exception {
        // Build a request JSON with an invalid gender value
        Map<String, Object> payload = Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123",
                "gender", "NOT_A_GENDER"
        );

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
