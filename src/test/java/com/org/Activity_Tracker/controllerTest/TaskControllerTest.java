package com.org.Activity_Tracker.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.Activity_Tracker.controllers.TaskController;
import com.org.Activity_Tracker.exceptions.GlobalExceptionHandler;
import com.org.Activity_Tracker.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TaskService taskService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        TaskController taskController = new TaskController(taskService, session);

        mockMvc = MockMvcBuilders
                .standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void updateTaskStatus_invalidStatus_shouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = Map.of("status", "NOT_A_STATUS");

        mockMvc.perform(patch("/api/v1/task/update-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void viewTaskByStatus_invalidStatus_shouldReturnEmptyList() throws Exception {
        when(taskService.viewTaskByStatus(eq("NOT_A_STATUS"), Mockito.<HttpSession>any()))
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/v1/task/view/NOT_A_STATUS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", empty()));
    }
}