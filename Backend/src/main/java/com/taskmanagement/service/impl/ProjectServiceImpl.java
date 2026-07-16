package com.taskmanagement.service.impl;

import com.taskmanagement.dto.event.NotificationEvent;
import com.taskmanagement.dto.request.CreateProjectRequest;
import com.taskmanagement.dto.request.UpdateProjectRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.ProjectResponse;
import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.User;
import com.taskmanagement.enums.NotificationType;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.exception.ForbiddenException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.kafka.NotificationEventProducer;
import com.taskmanagement.mapper.ProjectMapper;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.AuditService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.enums.AuditAction;
import com.taskmanagement.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final AuditService auditService;
    private final NotificationEventProducer notificationEventProducer;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              ProjectMapper projectMapper,
                              AuditService auditService,
                              NotificationEventProducer notificationEventProducer) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
        this.auditService = auditService;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = getCurrentUser();

        if (!SecurityUtil.isAdmin() && !SecurityUtil.isManager()) {
            throw new ForbiddenException("Only ADMIN or MANAGER can create projects");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() :
                        com.taskmanagement.enums.ProjectStatus.PLANNING)
                .owner(currentUser)
                .members(new HashSet<>())
                .build();

        project.getMembers().add(currentUser);
        project = projectRepository.save(project);

        auditService.logAudit(AuditAction.CREATE, "Project", String.valueOf(project.getId()),
                currentUser.getId(), null, "Project created: " + project.getName());

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponse> getAllProjects(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Project> projects = projectRepository.findByDeletedFalse(pageable);
        return PaginationUtil.toPaginatedResponse(projects.map(projectMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponse> getProjectsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Project> projects = projectRepository.findByMemberIdAndDeletedFalse(userId, pageable);
        return PaginationUtil.toPaginatedResponse(projects.map(projectMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponse> searchProjects(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Project> projects = projectRepository.searchProjects(query, pageable);
        return PaginationUtil.toPaginatedResponse(projects.map(projectMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ProjectResponse> getProjectsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        com.taskmanagement.enums.ProjectStatus projectStatus =
                com.taskmanagement.enums.ProjectStatus.valueOf(status);
        Page<Project> projects = projectRepository.findByStatusAndDeletedFalse(projectStatus, pageable);
        return PaginationUtil.toPaginatedResponse(projects.map(projectMapper::toResponse));
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        String oldValues = "name=" + project.getName() + ",status=" + project.getStatus();

        projectMapper.updateEntityFromRequest(request, project);
        project = projectRepository.save(project);

        String newValues = "name=" + project.getName() + ",status=" + project.getStatus();
        auditService.logAudit(AuditAction.UPDATE, "Project", String.valueOf(id),
                currentUser.getId(), oldValues, newValues);

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        User currentUser = getCurrentUser();
        if (!SecurityUtil.isAdmin() && !project.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only project owner or ADMIN can delete a project");
        }

        auditService.logAudit(AuditAction.DELETE, "Project", String.valueOf(id),
                currentUser.getId(), "name=" + project.getName(), null);

        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public ProjectResponse addMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (project.getMembers().contains(newMember)) {
            throw new BadRequestException("User is already a member of this project");
        }

        project.getMembers().add(newMember);
        project = projectRepository.save(project);

        auditService.logAudit(AuditAction.UPDATE, "Project", String.valueOf(projectId),
                currentUser.getId(), null, "Added member: " + newMember.getEmail());

        notificationEventProducer.sendNotificationEvent(
                NotificationEvent.of(
                        NotificationType.PROJECT_CREATED,
                        currentUser.getFullName() + " added you to project: " + project.getName(),
                        userId, projectId, "Project"
                )
        );

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse removeMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        User currentUser = getCurrentUser();
        checkProjectAccess(project, currentUser);

        User member = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Cannot remove the project owner");
        }

        if (!project.getMembers().contains(member)) {
            throw new BadRequestException("User is not a member of this project");
        }

        project.getMembers().remove(member);
        project = projectRepository.save(project);

        auditService.logAudit(AuditAction.UPDATE, "Project", String.valueOf(projectId),
                currentUser.getId(), null, "Removed member: " + member.getEmail());

        return projectMapper.toResponse(project);
    }

    private void checkProjectAccess(Project project, User user) {
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().contains(user);
        boolean isAdmin = SecurityUtil.isAdmin();

        if (!isOwner && !isMember && !isAdmin) {
            throw new ForbiddenException("You don't have access to this project");
        }
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Current user not found"));
    }
}
