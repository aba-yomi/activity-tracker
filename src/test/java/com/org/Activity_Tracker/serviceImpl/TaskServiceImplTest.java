package com.org.Activity_Tracker.serviceImpl;

import com.org.Activity_Tracker.entities.Task;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Status;
import com.org.Activity_Tracker.exceptions.ResourceNotFoundException;
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

        when(session.getAttribute("currUser")).thenReturn(user);

        TaskResponseDto response = (TaskResponseDto) taskService.viewTaskById(1L, session);

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

        when(session.getAttribute("currUser")).thenReturn(user);

        String result =  taskService.edit_taskTitle(dto, existingTask.getId(), session);

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

        when(session.getAttribute("currUser")).thenReturn(user);

        String result = taskService.edit_taskDescription(dto, existingTask.getId(), session);

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

        when(session.getAttribute("currUser")).thenReturn(user);

        String result = taskService.updateTaskStatus(dto, existingTask.getId(), session);

        assertEquals("Task status updated successfully", result);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteTask() {
        User user = createTestUser();
        Task existingTask = new Task(1L, "Title", "Desc", Status.PENDING, user);

        when(taskRepository.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        doNothing().when(taskRepository).delete(existingTask);

        when(session.getAttribute("currUser")).thenReturn(user);

        taskService.deleteTask(existingTask.getId(), session);

        verify(taskRepository).findById(existingTask.getId());
        verify(taskRepository).delete(existingTask);
    }

    @Test
    void shouldReturnMappedDtos_WhenTasksMatchQuery() {
        User owner = createTestUser();
        owner.setId(1L);

        Task task1 = new Task( "Title1", "Description1", Status.DONE, owner);
        Task task2 = new Task("Title2", "Description2", Status.IN_PROGRESS, owner);
        List<Task> tasks = List.of(task1, task2);

        when(session.getAttribute("currUser")).thenReturn(owner);
        when(taskRepository.searchByTitleOrDescriptionAndUserId("Title", owner.getId())).thenReturn(tasks);

        List<TaskResponseDto> result = taskService.searchTask("Title", session);

        assertEquals(2, result.size());

        assertEquals("Title1", result.get(0).getTitle());
        assertEquals("Description1", result.get(0).getDescription());
        assertEquals(Status.DONE, result.get(0).getStatus());

        assertEquals("Title2", result.get(1).getTitle());
        assertEquals("Description2", result.get(1).getDescription());
        assertEquals(Status.IN_PROGRESS, result.get(1).getStatus());

        verify(taskRepository).searchByTitleOrDescriptionAndUserId("Title", owner.getId());
    }

    @Test
    void shouldReturnEmptyList_WhenNoTasksFound() {
        User owner = createTestUser();
        owner.setId(1L);

        when(session.getAttribute("currUser")).thenReturn(owner);
        when(taskRepository.searchByTitleOrDescriptionAndUserId("nonexistent", owner.getId()))
                .thenReturn(Collections.emptyList());

        List<TaskResponseDto> result = taskService.searchTask("nonexistent", session);

        assertTrue(result.isEmpty());
        verify(taskRepository).searchByTitleOrDescriptionAndUserId("nonexistent", owner.getId());
    }

    @Test
    void shouldReturnEmptyList_WhenQueryIsNull() {
        User owner = createTestUser();
        owner.setId(1L);

        when(session.getAttribute("currUser")).thenReturn(owner);
        List<TaskResponseDto> result = taskService.searchTask(null, session);

        assertTrue(result.isEmpty());
        verify(taskRepository, never()).searchByTitleOrDescriptionAndUserId(any(), anyLong());
    }

    @Test
    void shouldReturnEmptyList_WhenQueryIsEmpty() {
        User owner = createTestUser();
        owner.setId(1L);

        when(session.getAttribute("currUser")).thenReturn(owner);
        List<TaskResponseDto> result = taskService.searchTask("", session);

        assertTrue(result.isEmpty());
        verify(taskRepository, never()).searchByTitleOrDescriptionAndUserId(any(), anyLong());
    }


    @Test
    void viewTaskById_nonOwner_throwsResourceNotFound() {
        User owner = createTestUser();
        owner.setId(2L);
        Task existingTask = Task.builder()
                .id(10L)
                .title("Owner Task")
                .description("Secret")
                .status(Status.PENDING)
                .user(owner)
                .build();

        when(taskRepository.findById(10L)).thenReturn(Optional.of(existingTask));

        User currentUser = createTestUser();
        currentUser.setId(1L);
        when(session.getAttribute("currUser")).thenReturn(currentUser);

        assertThrows(ResourceNotFoundException.class, () -> taskService.viewTaskById(10L, session));

        verify(taskRepository).findById(10L);
    }

    @Test
    void deleteTask_nonOwner_throwsResourceNotFound() {
        User owner = createTestUser();
        owner.setId(2L);
        Task existingTask = new Task(5L, "T", "D", Status.PENDING, owner);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(existingTask));

        User currentUser = createTestUser();
        currentUser.setId(1L);
        when(session.getAttribute("currUser")).thenReturn(currentUser);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(5L, session));
        verify(taskRepository).findById(5L);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void searchTask_onlyReturnsTasksForCurrentUser() {
        User owner = createTestUser();
        owner.setId(1L);

        Task ownedTask = new Task("TitleX", "Desc", Status.DONE, owner);

        when(session.getAttribute("currUser")).thenReturn(owner);
        when(taskRepository.searchByTitleOrDescriptionAndUserId("Title", owner.getId()))
                .thenReturn(List.of(ownedTask));

        List<TaskResponseDto> results = taskService.searchTask("Title", session);

        assertEquals(1, results.size());
        assertEquals("TitleX", results.get(0).getTitle());

        verify(taskRepository).searchByTitleOrDescriptionAndUserId("Title", owner.getId());
    }

    @Test
    void edit_taskTitle_nonOwner_throwsResourceNotFoundAndDoesNotSave() {
        User owner = createTestUser();
        owner.setId(2L);
        Task existingTask = new Task(7L, "Old Title", "Desc", Status.PENDING, owner);

        when(taskRepository.findById(7L)).thenReturn(Optional.of(existingTask));

        User current = createTestUser();
        current.setId(1L);
        when(session.getAttribute("currUser")).thenReturn(current);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("New Title");

        assertThrows(ResourceNotFoundException.class, () -> taskService.edit_taskTitle(dto, 7L, session));

        verify(taskRepository).findById(7L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void edit_taskTitle_owner_succeeds() {
        User owner = createTestUser();
        owner.setId(1L);
        Task existingTask = new Task(7L, "Old Title", "Desc", Status.PENDING, owner);

        when(taskRepository.findById(7L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(session.getAttribute("currUser")).thenReturn(owner);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setTitle("New Title");

        String res = taskService.edit_taskTitle(dto, 7L, session);

        assertEquals("Task updated sucessfully", res);
        verify(taskRepository).save(existingTask);
        assertEquals("New Title", existingTask.getTitle());
    }

    @Test
    void edit_taskDescription_nonOwner_throwsResourceNotFoundAndDoesNotSave() {
        User owner = createTestUser();
        owner.setId(3L);
        Task existingTask = new Task(8L, "Title", "Old desc", Status.PENDING, owner);

        when(taskRepository.findById(8L)).thenReturn(Optional.of(existingTask));

        User current = createTestUser();
        current.setId(1L);
        when(session.getAttribute("currUser")).thenReturn(current);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setDescription("New desc");

        assertThrows(ResourceNotFoundException.class, () -> taskService.edit_taskDescription(dto, 8L, session));

        verify(taskRepository).findById(8L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void edit_taskDescription_owner_succeeds() {
        User owner = createTestUser();
        owner.setId(1L);
        Task existingTask = new Task(8L, "Title", "Old desc", Status.PENDING, owner);

        when(taskRepository.findById(8L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(session.getAttribute("currUser")).thenReturn(owner);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setDescription("New desc");

        String res = taskService.edit_taskDescription(dto, 8L, session);

        assertEquals("Task updated sucessfully", res);
        verify(taskRepository).save(existingTask);
        assertEquals("New desc", existingTask.getDescription());
    }

    @Test
    void updateTaskStatus_nonOwner_throwsResourceNotFoundAndDoesNotSave() {
        User owner = createTestUser();
        owner.setId(4L);
        Task existingTask = new Task(9L, "Title", "Desc", Status.PENDING, owner);

        when(taskRepository.findById(9L)).thenReturn(Optional.of(existingTask));

        User current = createTestUser();
        current.setId(1L);
        when(session.getAttribute("currUser")).thenReturn(current);

        TaskRequestDto dto = new TaskRequestDto();
        dto.setStatus("DONE");

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskStatus(dto, 9L, session));

        verify(taskRepository).findById(9L);
        verify(taskRepository, never()).save(any());
    }


    @Test
    void viewTaskByStatus_returnsOnlyCurrentUserTasks() {
        User owner = createTestUser();
        owner.setId(1L);

        Task t1 = new Task(11L, "T1", "D1", Status.DONE, owner);

        when(session.getAttribute("currUser")).thenReturn(owner);
        when(taskRepository.findAllByUserIdAndStatus(eq(owner.getId()), eq(Status.DONE)))
                .thenReturn(List.of(t1));

        List<TaskResponseDto> results = taskService.viewTaskByStatus("DONE", session);

        assertEquals(1, results.size(), "Expected exactly one result for the current user's DONE tasks");
        assertEquals("T1", results.get(0).getTitle());
        assertEquals("D1", results.get(0).getDescription());
        assertEquals(Status.DONE, results.get(0).getStatus());

        verify(taskRepository).findAllByUserIdAndStatus(eq(owner.getId()), eq(Status.DONE));
    }

}
