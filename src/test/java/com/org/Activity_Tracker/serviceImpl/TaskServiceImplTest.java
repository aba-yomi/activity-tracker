package com.org.Activity_Tracker.serviceImpl;

import com.org.Activity_Tracker.entities.Task;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Gender;
import com.org.Activity_Tracker.enums.Status;
import com.org.Activity_Tracker.pojos.TaskRequestDto;
import com.org.Activity_Tracker.pojos.TaskResponseDto;
import com.org.Activity_Tracker.repositories.TaskRepository;
import com.org.Activity_Tracker.repositories.UserRepository;
import com.org.Activity_Tracker.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TaskServiceImplTest {

    @Autowired UserRepository userRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired TaskService taskService;
    @Autowired HttpSession session;

    private User testUser;
    private Task existingTask;

    @BeforeEach
    void setup() {
        testUser = userRepository.findByEmail("musty@example.com")
                .orElseGet(() -> userRepository.save(
                        new User("musty", "musty@example.com", "1234", Gender.MALE)
                ));

        session.setAttribute("currUser", testUser);

        existingTask = new Task("Initial Task", "Sample description", Status.PENDING, testUser);
        existingTask = taskRepository.save(existingTask);
    }


    @Test
    void createTask() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Yom");
        dto.setDescription("Have a talk with him");

        taskService.createTask(dto, session);

        List<Task> tasks = taskRepository.findAll();
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Yom")));
    }

    @Test
    void viewTaskById() {
        TaskResponseDto response = (TaskResponseDto) taskService.viewTaskById(existingTask.getId());

        assertNotNull(response);
        assertEquals("Initial Task", response.getTitle());
    }

    @Test
    void edit_taskTitle() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated Title");

        taskService.edit_taskTitle(dto, existingTask.getId());

        Task updatedTask = taskRepository.findById(existingTask.getId()).orElseThrow();
        assertEquals("Updated Title", updatedTask.getTitle());
    }

    @Test
    void edit_taskDescription() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setDescription("New description");

        taskService.edit_taskDescription(dto, existingTask.getId());

        Task updatedTask = taskRepository.findById(existingTask.getId()).orElseThrow();
        assertEquals("New description", updatedTask.getDescription());
    }

    @Test
    void updateTaskStatus() {
        TaskRequestDto dto = new TaskRequestDto();
        dto.setStatus("IN_PROGRESS");

        taskService.updateTaskStatus(dto, existingTask.getId());

        Task updatedTask = taskRepository.findById(existingTask.getId()).orElseThrow();
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }

//    @Test
//    void viewTaskByStatus() {
//        TaskRequestDto dto = new TaskRequestDto();
//        dto.setStatus("DONE");
//        taskService.updateTaskStatus(dto, existingTask.getId());
//
//        List<TaskResponseDto> response = taskService.viewTaskByStatus("DONE", session);
//
//
//        assertNotNull(response);
//        assertEquals(1, response.size());
//        assertEquals("DONE", response.get(0).getStatus().name());
//    }

    @Test
    void deleteTask() {
        taskService.deleteTask(existingTask.getId());

        boolean exists = taskRepository.existsById(existingTask.getId());
        assertFalse(exists);
    }
}
