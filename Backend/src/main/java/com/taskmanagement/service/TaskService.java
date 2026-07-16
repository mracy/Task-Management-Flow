package com.taskmanagement.service;

import com.taskmanagement.dto.request.AssignTaskRequest;
import com.taskmanagement.dto.request.ChangeTaskStatusRequest;
import com.taskmanagement.dto.request.CreateTaskRequest;
import com.taskmanagement.dto.request.UpdateTaskRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.TaskResponse;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTaskById(Long id);

    PaginatedResponse<TaskResponse> getAllTasks(int page, int size, String sortBy, String sortDir);

    PaginatedResponse<TaskResponse> getTasksByProject(Long projectId, int page, int size);

    PaginatedResponse<TaskResponse> getTasksByAssignee(Long assigneeId, int page, int size);

    PaginatedResponse<TaskResponse> getTasksByStatus(String status, int page, int size);

    PaginatedResponse<TaskResponse> getTasksByPriority(String priority, int page, int size);

    PaginatedResponse<TaskResponse> searchTasks(String query, int page, int size);

    PaginatedResponse<TaskResponse> filterTasks(Long projectId, String status, String priority,
                                                 Long assigneeId, int page, int size,
                                                 String sortBy, String sortDir);

    TaskResponse updateTask(Long id, UpdateTaskRequest request);

    TaskResponse assignTask(Long id, AssignTaskRequest request);

    TaskResponse changeStatus(Long id, ChangeTaskStatusRequest request);

    void deleteTask(Long id);
}
