package com.org.Activity_Tracker.services.serviceImpl;

import com.org.Activity_Tracker.entities.Task;
import com.org.Activity_Tracker.entities.User;
import com.org.Activity_Tracker.enums.Status;
import com.org.Activity_Tracker.exceptions.ResourceNotFoundException;
import com.org.Activity_Tracker.exceptions.TypeMismatchException;
import com.org.Activity_Tracker.exceptions.UserNotFoundException;
import com.org.Activity_Tracker.pojos.TaskRequestDto;
import com.org.Activity_Tracker.pojos.TaskResponseDto;
import com.org.Activity_Tracker.repositories.TaskRepository;
import com.org.Activity_Tracker.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;


//    ===========================CREATE TASK=============================================
    @Override
    public String createTask(TaskRequestDto request, HttpSession session) {

        User user = (User) session.getAttribute("currUser");
        if(user != null) {
            Task task = Task.builder()
                    .user(user)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(Status.valueOf("PENDING")).build();

            taskRepository.save(task);

            return "Task created successfully";
        }
        throw new UserNotFoundException("Login to create a tasks", "No user in session");
    }


    //    ===========================VIEW ALL TASK=============================================
    @Override
    public Object viewAllTask(HttpSession session) {

        User user = (User) session.getAttribute("currUser");

        if(user != null) {
            List<Task> tasks = user.getTasks();
            List<TaskResponseDto> taskList = new ArrayList<>();
            tasks.forEach(task -> {

                TaskResponseDto response = TaskResponseDto.builder()
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .build();
                taskList.add(response);
            });
            return taskList;
        }
        throw new UserNotFoundException("Login to view your tasks", "No user in session");
    }

    //    ===========================VIEW TASK BY ID=============================================
    @Override
    public Object viewTaskById(Long task_id, HttpSession session) {

        Task task = findTaskForCurrentUser(task_id, session);

        return TaskResponseDto.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .build();
    }

//    ===========================EDIT TASK TITLE=============================================

    public String edit_taskTitle(TaskRequestDto request, Long task_id, HttpSession session){
        Task task = findTaskForCurrentUser(task_id, session);
        task.setTitle(request.getTitle());
        taskRepository.save(task);
        return "Task updated sucessfully";
    }


    //    ===========================EDIT TASK DESCRIPTION=============================================

    @Override
    public String edit_taskDescription(TaskRequestDto request, Long task_id, HttpSession session){
        Task task = findTaskForCurrentUser(task_id, session);
        task.setDescription(request.getDescription());
        taskRepository.save(task);
        return "Task updated sucessfully";
    }


    //    ===========================DELETE TASK=============================================

    @Override
    public String deleteTask(Long task_id, HttpSession session) {
        Task task = findTaskForCurrentUser(task_id, session);
        taskRepository.delete(task);
        return "Task deleted successfully!";
    }


    @Override
    public List<TaskResponseDto> viewTaskByStatus(String status, HttpSession session){
        User user = getSessionUser(session);
        if (status == null || status.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Status statusEnum;
        try {
            statusEnum = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }

        Long userId = user.getId();
        List<Task> tasks = taskRepository.findAllByUserIdAndStatus(userId, statusEnum);

        List<TaskResponseDto> responses = new ArrayList<>();
        if (tasks != null) {
            tasks.forEach(task -> {
                TaskResponseDto response = TaskResponseDto.builder()
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .build();
                responses.add(response);
            });
        }
        return responses;
    }


    //    ===========================UPDATE TASK STATUS=============================================
    @Override
    public String updateTaskStatus(TaskRequestDto request, Long task_id, HttpSession session) {
        Task task = findTaskForCurrentUser(task_id, session);
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            throw new TypeMismatchException("Status is required");
        }
        try{
            task.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
            if("done".equalsIgnoreCase(request.getStatus())){
                task.setCompletedAt(new Date());
            }
            taskRepository.save(task);
            return "Task status updated successfully";
        }catch (IllegalArgumentException ex){
            throw new TypeMismatchException("Invalid Status passed");
        }

    }

    @Override
    public List<TaskResponseDto> searchTask(String query, HttpSession session){
        User user = getSessionUser(session);
        Long userId = user.getId();

        // null/blank query handling — return empty list (or you could choose to return all user's tasks)
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Task> matchingTasks = taskRepository.searchByTitleOrDescriptionAndUserId(query, userId);

        return matchingTasks.stream()
                .map(task -> TaskResponseDto.builder()
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .build()
                )
                .toList();
    }



    private User getSessionUser(HttpSession session){
        User user = (User) session.getAttribute("currUser");
        if(user == null) throw new UserNotFoundException("Login required", "No user in session");
        return user;
    }

    private Task findTaskForCurrentUser(Long taskId, HttpSession session){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found", "Provide a valid taskId"));

        User currUser = getSessionUser(session);
        if (task.getUser() == null || !task.getUser().getId().equals(currUser.getId())) {
            throw new ResourceNotFoundException("Task not found", "Provide a valid taskId");
        }
        return task;
    }
}
