package com.taskmanagement.service.impl;

import com.taskmanagement.dto.event.NotificationEvent;
import com.taskmanagement.dto.event.TaskEvent;
import com.taskmanagement.dto.request.AssignTaskRequest;
import com.taskmanagement.dto.request.ChangeTaskStatusRequest;
import com.taskmanagement.dto.request.CreateTaskRequest;
import com.taskmanagement.dto.request.UpdateTaskRequest;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.dto.response.TaskResponse;
import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.User;
import com.taskmanagement.enums.NotificationType;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.exception.ForbiddenException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.kafka.NotificationEventProducer;
import com.taskmanagement.kafka.TaskEventProducer;
import com.taskmanagement.mapper.TaskMapper;
import com.taskmanagement.repository.CommentRepository;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.AuditService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.enums.AuditAction;
import com.taskmanagement.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final AuditService auditService;
    private final TaskEventProducer taskEventProducer;
    private final NotificationEventProducer notificationEventProducer;

    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           TaskMapper taskMapper,
                           AuditService auditService,
                           TaskEventProducer taskEventProducer,
                           NotificationEventProducer notificationEventProducer) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.taskMapper = taskMapper;
        this.auditService = auditService;
        this.taskEventProducer = taskEventProducer;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        User currentUser = getCurrentUser();
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() :
                        com.taskmanagement.enums.Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .project(project)
                .reporter(currentUser)
                .orderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0)
                .build();

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        task = taskRepository.save(task);

        final Task savedTask = task;
        auditService.logAudit(AuditAction.CREATE, "Task", String.valueOf(task.getId()),
                currentUser.getId(), null, "Task created: " + task.getTitle());

        taskEventProducer.sendTaskCreatedEvent(
                TaskEvent.of("task-created", savedTask.getId(), savedTask.getTitle(),
                        savedTask.getProject().getId(),
                        savedTask.getAssignee() != null ? savedTask.getAssignee().getId() : null,
                        savedTask.getReporter().getId(),
                        null, savedTask.getStatus().name())
        );

        if (task.getAssignee() != null && !task.getAssignee().getId().equals(currentUser.getId())) {
            notificationEventProducer.sendNotificationEvent(
                    NotificationEvent.of(
                            NotificationType.TASK_ASSIGNED,
                            currentUser.getFullName() + " assigned you a task: " + task.getTitle(),
                            task.getAssignee().getId(), task.getId(), "Task"
                    )
            );
        }

        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount(0);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount((int) commentRepository.countByTaskIdAndDeletedFalse(id));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getAllTasks(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> tasks = taskRepository.findByDeletedFalse(pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getTasksByProject(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderIndex").ascending());
        Page<Task> tasks = taskRepository.findByProjectIdAndDeletedFalse(projectId, pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getTasksByAssignee(Long assigneeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> tasks = taskRepository.findByAssigneeIdAndDeletedFalse(assigneeId, pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getTasksByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        TaskStatus taskStatus = TaskStatus.valueOf(status);
        Page<Task> tasks = taskRepository.findByStatusAndDeletedFalse(taskStatus, pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> getTasksByPriority(String priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        com.taskmanagement.enums.Priority taskPriority = com.taskmanagement.enums.Priority.valueOf(priority);
        Page<Task> tasks = taskRepository.findByPriorityAndDeletedFalse(taskPriority, pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> searchTasks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> tasks = taskRepository.searchTasks(query, pageable);
        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TaskResponse> filterTasks(Long projectId, String status, String priority,
                                                        Long assigneeId, int page, int size,
                                                        String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Use a dynamic query approach
        // For simplicity, we'll chain filters based on what's provided
        Page<Task> tasks;

        if (projectId != null && status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status);
            tasks = taskRepository.findByProjectIdAndStatusAndDeletedFalse(projectId, taskStatus, pageable);
        } else if (projectId != null && priority != null) {
            com.taskmanagement.enums.Priority taskPriority = com.taskmanagement.enums.Priority.valueOf(priority);
            tasks = taskRepository.findByProjectIdAndPriorityAndDeletedFalse(projectId, taskPriority, pageable);
        } else if (assigneeId != null && status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status);
            tasks = taskRepository.findByAssigneeIdAndStatusAndDeletedFalse(assigneeId, taskStatus, pageable);
        } else if (projectId != null) {
            tasks = taskRepository.findByProjectIdAndDeletedFalse(projectId, pageable);
        } else if (assigneeId != null) {
            tasks = taskRepository.findByAssigneeIdAndDeletedFalse(assigneeId, pageable);
        } else if (status != null) {
            TaskStatus taskStatus = TaskStatus.valueOf(status);
            tasks = taskRepository.findByStatusAndDeletedFalse(taskStatus, pageable);
        } else if (priority != null) {
            com.taskmanagement.enums.Priority taskPriority = com.taskmanagement.enums.Priority.valueOf(priority);
            tasks = taskRepository.findByPriorityAndDeletedFalse(taskPriority, pageable);
        } else {
            tasks = taskRepository.findByDeletedFalse(pageable);
        }

        return PaginationUtil.toPaginatedResponse(tasks.map(this::mapTaskWithCommentCount));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();
        checkTaskAccess(task, currentUser);

        String oldStatus = task.getStatus().name();
        String oldValues = "title=" + task.getTitle() + ",status=" + task.getStatus();

        taskMapper.updateEntityFromRequest(request, task);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
            task.setAssignee(assignee);
        } else if (request.getAssigneeId() == null && request.getStatus() != null) {
            // Don't clear assignee if only status changed
        }

        task = taskRepository.save(task);

        String newValues = "title=" + task.getTitle() + ",status=" + task.getStatus();
        auditService.logAudit(AuditAction.UPDATE, "Task", String.valueOf(id),
                currentUser.getId(), oldValues, newValues);

        taskEventProducer.sendTaskUpdatedEvent(
                TaskEvent.of("task-updated", task.getId(), task.getTitle(),
                        task.getProject().getId(),
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getReporter().getId(),
                        oldStatus, task.getStatus().name())
        );

        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount((int) commentRepository.countByTaskIdAndDeletedFalse(id));
        return response;
    }

    @Override
    @Transactional
    public TaskResponse assignTask(Long id, AssignTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();
        checkTaskAccess(task, currentUser);

        User previousAssignee = task.getAssignee();

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null);
        }

        task = taskRepository.save(task);

        auditService.logAudit(AuditAction.ASSIGN, "Task", String.valueOf(id),
                currentUser.getId(),
                previousAssignee != null ? "assignee=" + previousAssignee.getId() : "assignee=null",
                task.getAssignee() != null ? "assignee=" + task.getAssignee().getId() : "assignee=null");

        taskEventProducer.sendTaskAssignedEvent(
                TaskEvent.of("task-assigned", task.getId(), task.getTitle(),
                        task.getProject().getId(),
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getReporter().getId(),
                        previousAssignee != null ? previousAssignee.getId().toString() : null,
                        task.getAssignee() != null ? task.getAssignee().getId().toString() : null)
        );

        if (task.getAssignee() != null && !task.getAssignee().getId().equals(currentUser.getId())) {
            notificationEventProducer.sendNotificationEvent(
                    NotificationEvent.of(
                            NotificationType.TASK_ASSIGNED,
                            currentUser.getFullName() + " assigned you a task: " + task.getTitle(),
                            task.getAssignee().getId(), task.getId(), "Task"
                    )
            );
        }

        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount((int) commentRepository.countByTaskIdAndDeletedFalse(id));
        return response;
    }

    @Override
    @Transactional
    public TaskResponse changeStatus(Long id, ChangeTaskStatusRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();
        checkTaskAccess(task, currentUser);

        String oldStatus = task.getStatus().name();
        task.setStatus(request.getStatus());
        task = taskRepository.save(task);

        auditService.logAudit(AuditAction.STATUS_CHANGE, "Task", String.valueOf(id),
                currentUser.getId(),
                "status=" + oldStatus,
                "status=" + task.getStatus().name());

        taskEventProducer.sendTaskUpdatedEvent(
                TaskEvent.of("task-status-changed", task.getId(), task.getTitle(),
                        task.getProject().getId(),
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getReporter().getId(),
                        oldStatus, task.getStatus().name())
        );

        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount((int) commentRepository.countByTaskIdAndDeletedFalse(id));
        return response;
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        User currentUser = getCurrentUser();
        if (!SecurityUtil.isAdmin()
                && !task.getReporter().getId().equals(currentUser.getId())
                && !task.getProject().getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You don't have permission to delete this task");
        }

        auditService.logAudit(AuditAction.DELETE, "Task", String.valueOf(id),
                currentUser.getId(), "title=" + task.getTitle(), null);

        taskRepository.delete(task);
    }

    private TaskResponse mapTaskWithCommentCount(Task task) {
        TaskResponse response = taskMapper.toResponse(task);
        response.setCommentCount((int) commentRepository.countByTaskIdAndDeletedFalse(task.getId()));
        return response;
    }

    private void checkTaskAccess(Task task, User user) {
        boolean isReporter = task.getReporter().getId().equals(user.getId());
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
        boolean isProjectOwner = task.getProject().getOwner().getId().equals(user.getId());
        boolean isAdmin = SecurityUtil.isAdmin();

        if (!isReporter && !isAssignee && !isProjectOwner && !isAdmin) {
            throw new ForbiddenException("You don't have access to modify this task");
        }
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Current user not found"));
    }
}
