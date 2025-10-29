package com.org.Activity_Tracker.controllers;


import com.org.Activity_Tracker.services.TaskService;
import com.org.Activity_Tracker.utils.ResponseManager;
import com.org.Activity_Tracker.pojos.ApiResponse;
import com.org.Activity_Tracker.pojos.TaskRequestDto;
import com.org.Activity_Tracker.pojos.TaskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;
    private final HttpSession session;

    //==========================CREATE TASK=======================================================

    @PostMapping("/create")
    public ApiResponse<Object>  createTask(@Valid @RequestBody TaskRequestDto request){
        String response = taskService.createTask(request, session);
        return new ResponseManager().success(response, HttpStatus.CREATED);
    }


    //==========================VIEW ALL TASK=======================================================

    @GetMapping("/view-all")
    public ApiResponse<Object> getAllTask(){
        Object response = taskService.viewAllTask(session);
        return new ResponseManager().success(response, HttpStatus.FOUND);
    }


    //==========================VIEW TASK BY ID=======================================================

    @GetMapping("/view-task-by-id/{task_id}")
    public ApiResponse<Object> viewTaskById(@PathVariable Long task_id){
        Object response = taskService.viewTaskById(task_id);
        return new ResponseManager().success(response, HttpStatus.FOUND);
    }


    //==========================DELETE TASK============================================================

    @DeleteMapping("/delete/{task_id}")
    public ApiResponse<Object> deleteTask(@PathVariable Long task_id){
        String response = taskService.deleteTask(task_id);
        return new ResponseManager().success(response, HttpStatus.OK);
    }


    //==========================VIEW TASK BY STATUS=======================================================

    @GetMapping("/view/{status}")
    public ApiResponse<Object> viewTaskByStatus(@PathVariable String status){
        List<TaskResponseDto> response = taskService.viewTaskByStatus(status, session);
        return new ResponseManager().success(response, HttpStatus.FOUND);
    }

    //==========================EDIT TASK TITLE=======================================================
    @PatchMapping("/edit-title/{task_id}")
    public ApiResponse<Object> edit_taskTitle(@Valid @RequestBody TaskRequestDto request, @PathVariable Long task_id){
        String response = taskService.edit_taskTitle(request, task_id);
        return new ResponseManager().success(response, HttpStatus.OK);
    }

    //==========================EDIT TASK DESCRIPTION=======================================================
    @PatchMapping("/edit-description/{task_id}")
    public ApiResponse<Object> edit_taskDescription(@Valid @RequestBody TaskRequestDto request, @PathVariable Long task_id){
        String response = taskService.edit_taskDescription(request, task_id);
        return new ResponseManager().success(response, HttpStatus.OK);
    }

    //==========================UPDATE TASK STATUS=======================================================
    @PatchMapping("/update-status/{task_id}")
    public ApiResponse<Object> updateStatus(@Valid @RequestBody TaskRequestDto request, @PathVariable Long task_id){
        String response = taskService.updateTaskStatus(request, task_id);
        return new ResponseManager().success(response, HttpStatus.OK);
    }

}
