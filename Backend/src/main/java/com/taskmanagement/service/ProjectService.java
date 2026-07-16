package com.taskmanagement.service;

import com.taskmanagement.dto.request.CreateProjectRequest;
import com.taskmanagement.dto.request.UpdateProjectRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.ProjectResponse;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse getProjectById(Long id);

    PaginatedResponse<ProjectResponse> getAllProjects(int page, int size, String sortBy, String sortDir);

    PaginatedResponse<ProjectResponse> getProjectsByUser(Long userId, int page, int size);

    PaginatedResponse<ProjectResponse> searchProjects(String query, int page, int size);

    PaginatedResponse<ProjectResponse> getProjectsByStatus(String status, int page, int size);

    ProjectResponse updateProject(Long id, UpdateProjectRequest request);

    void deleteProject(Long id);

    ProjectResponse addMember(Long projectId, Long userId);

    ProjectResponse removeMember(Long projectId, Long userId);
}
