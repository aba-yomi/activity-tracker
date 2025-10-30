package com.org.Activity_Tracker.serviceImpl;

import com.org.Activity_Tracker.entities.Task;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Status;
import com.org.Activity_Tracker.pojos.TaskRequestDto;
import com.org.Activity_Tracker.pojos.TaskResponseDto;
import com.org.Activity_Tracker.repositories.TaskRepository;
import com.org.Activity_Tracker.services.serviceImpl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository);
    }


    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        return user;
    }


    @Test
    void createTask() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test1");
        mockUser.setPassword("12345678");

        when(session.getAttribute("currUser")).thenReturn(mockUser);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Yom");
        dto.setDescription("Have a talk with him");

        String result = taskService.createTask(dto, session);
        assertEquals("Task created successfully", result);
        verify(taskRepository, times(1)).save(any(Task.class));

    }


    @Test
    void viewTaskById() {
        User user = createTestUser();
        Task existingTask = Task.builder()
                .id(1L)
                .title("Initial Task")
                .description("Desc")
                .status(Status.IN_PROGRESS)
                .user(user)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        TaskResponseDto response = (TaskResponseDto) taskService.viewTaskById(1L);

        assertNotNull(response);
        assertEquals("Initial Task", response.getTitle());
        assertEquals("Desc", response.getDescription());
        assertEquals(Status.IN_PROGRESS, response.getStatus());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void edit_taskTitle() {
        User user = createTestUser();
        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("Updated Title");

        Task existingTask = new Task(1L, "Old Title", "Desc", Status.DONE, user);
        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result =  taskService.edit_taskTitle(dto, existingTask.getId());

        assertEquals("Task updated sucessfully", result);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void edit_taskDescription() {
        User user = createTestUser();
        TaskRequestDto dto = new TaskRequestDto();
        dto.setDescription("New description");

        Task existingTask = new Task(1L, "Title", "Old description", Status.PENDING, user);
        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = taskService.edit_taskDescription(dto, existingTask.getId());

        assertEquals("Task updated sucessfully", result);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTaskStatus() {
        User user = createTestUser();
        TaskRequestDto dto = new TaskRequestDto();
        dto.setStatus("IN_PROGRESS");

        Task existingTask = new Task(1L, "Title", "Desc", Status.IN_PROGRESS, user);
        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = taskService.updateTaskStatus(dto, existingTask.getId());

        assertEquals("Task status updated successfully", result);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteTask() {
        User user = createTestUser();
        Task existingTask = new Task(1L, "Title", "Desc", Status.PENDING, user);

        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).delete(existingTask);

        taskService.deleteTask(existingTask.getId());

        verify(taskRepository).findById(existingTask.getId());
        verify(taskRepository).delete(existingTask);
    }

    @Test
    void shouldReturnMappedDtos_WhenTasksMatchQuery() {
        Task task1 = new Task( "Title1", "Description1", Status.DONE, createTestUser());
        Task task2 = new Task("Title2", "Description2", Status.IN_PROGRESS, createTestUser());
        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.searchByTitleOrDescription("Title")).thenReturn(tasks);

        List<TaskResponseDto> result = taskService.searchTask("Title");

        assertEquals(2, result.size());

        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Description1", result.get(0).getDescription());
        assertEquals(Status.DONE, result.get(0).getStatus());

        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Description2", result.get(1).getDescription());
        assertEquals(Status.IN_PROGRESS, result.get(1).getStatus());

        verify(taskRepository).searchByTitleOrDescription("Title");
    }

    @Test
    void shouldReturnEmptyList_WhenNoTasksFound() {
        when(taskRepository.searchByTitleOrDescription("nonexistent"))
                .thenReturn(Collections.emptyList());

        List<TaskResponseDto> result = taskService.searchTask("nonexistent");

        assertTrue(result.isEmpty());
        verify(taskRepository).searchByTitleOrDescription("nonexistent");
    }

    @Test
    void shouldReturnEmptyList_WhenQueryIsNull() {
        when(taskRepository.searchByTitleOrDescription(null))
                .thenReturn(Collections.emptyList());

        List<TaskResponseDto> result = taskService.searchTask(null);

        assertTrue(result.isEmpty());
        verify(taskRepository).searchByTitleOrDescription(null);
    }

    @Test
    void shouldReturnEmptyList_WhenQueryIsEmpty() {
        when(taskRepository.searchByTitleOrDescription("")).thenReturn(Collections.emptyList());

        List<TaskResponseDto> result = taskService.searchTask("");

        assertTrue(result.isEmpty());
        verify(taskRepository).searchByTitleOrDescription("");
    }
}
