package com.org.Activity_Tracker.services;


import com.org.Activity_Tracker.pojos.TaskRequestDto;
import com.org.Activity_Tracker.pojos.TaskResponseDto;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface TaskService {

    String createTask(TaskRequestDto request, String username);

    Object viewAllTask(HttpSession session);

    Object viewTaskById(Long task_id);

    String deleteTask(Long task_id);

    String edit_taskTitle(TaskRequestDto request, Long task_id);

    String edit_taskDescription(TaskRequestDto request, Long task_id);

    List<TaskResponseDto> viewTaskByStatus(String status, HttpSession session);

    String updateTaskStatus (TaskRequestDto request, Long task_id);

}
