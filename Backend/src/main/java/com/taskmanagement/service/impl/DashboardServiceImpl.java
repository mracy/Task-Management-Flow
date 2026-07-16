package com.taskmanagement.service.impl;

import com.taskmanagement.dto.response.DashboardResponse;
import com.taskmanagement.entity.AuditLog;
import com.taskmanagement.entity.User;
import com.taskmanagement.enums.AuditAction;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.ProjectStatus;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.repository.AuditLogRepository;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.DashboardService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DASHBOARD_CACHE_KEY = "dashboard:stats";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public DashboardServiceImpl(ProjectRepository projectRepository,
                                TaskRepository taskRepository,
                                UserRepository userRepository,
                                AuditLogRepository auditLogRepository,
                                RedisTemplate<String, Object> redisTemplate) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public DashboardResponse getDashboardStats() {
        // Try cache first
        try {
            Object cached = redisTemplate.opsForValue().get(DASHBOARD_CACHE_KEY);
            if (cached instanceof DashboardResponse) {
                return (DashboardResponse) cached;
            }
        } catch (Exception e) {
            // Cache miss or error, continue with DB
        }

        DashboardResponse stats = buildDashboardStats();

        // Cache the result
        try {
            redisTemplate.opsForValue().set(DASHBOARD_CACHE_KEY, stats, CACHE_TTL);
        } catch (Exception e) {
            // Cache write error, continue
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStatsByUser(Long userId) {
        DashboardResponse stats = buildDashboardStats();
        return stats;
    }

    private DashboardResponse buildDashboardStats() {
        long totalProjects = projectRepository.countActiveProjects();
        long activeProjects = projectRepository.countByStatus(ProjectStatus.IN_PROGRESS)
                + projectRepository.countByStatus(ProjectStatus.PLANNING);
        long totalTasks = taskRepository.countActiveTasks();
        long completedTasks = taskRepository.countByStatus(TaskStatus.DONE);
        long pendingTasks = taskRepository.countByStatus(TaskStatus.TODO)
                + taskRepository.countByStatus(TaskStatus.IN_PROGRESS)
                + taskRepository.countByStatus(TaskStatus.IN_REVIEW);
        long overdueTasks = taskRepository.countOverdueTasks(java.time.LocalDate.now());
        long totalUsers = userRepository.countActiveUsers();

        // Recent activities from audit logs
        List<AuditLog> recentLogs = auditLogRepository.findRecentActivity(LocalDateTime.now().minusDays(7));
        List<DashboardResponse.RecentActivity> recentActivities = recentLogs.stream()
                .limit(10)
                .map(log -> DashboardResponse.RecentActivity.builder()
                        .action(log.getAction().name())
                        .description(log.getEntityName() + " - " + log.getAction().name())
                        .userName(log.getUserId() != null ? "User #" + log.getUserId() : "System")
                        .timestamp(log.getTimestamp())
                        .build())
                .collect(Collectors.toList());

        // Tasks by status
        List<DashboardResponse.TaskByStatus> tasksByStatus = List.of(
                DashboardResponse.TaskByStatus.builder().status("TODO").count(taskRepository.countByStatus(TaskStatus.TODO)).build(),
                DashboardResponse.TaskByStatus.builder().status("IN_PROGRESS").count(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).build(),
                DashboardResponse.TaskByStatus.builder().status("IN_REVIEW").count(taskRepository.countByStatus(TaskStatus.IN_REVIEW)).build(),
                DashboardResponse.TaskByStatus.builder().status("DONE").count(taskRepository.countByStatus(TaskStatus.DONE)).build(),
                DashboardResponse.TaskByStatus.builder().status("CANCELLED").count(taskRepository.countByStatus(TaskStatus.CANCELLED)).build()
        );

        // Tasks by priority
        List<DashboardResponse.TaskByPriority> tasksByPriority = List.of(
                DashboardResponse.TaskByPriority.builder().priority("LOW").count(taskRepository.countByPriority(Priority.LOW)).build(),
                DashboardResponse.TaskByPriority.builder().priority("MEDIUM").count(taskRepository.countByPriority(Priority.MEDIUM)).build(),
                DashboardResponse.TaskByPriority.builder().priority("HIGH").count(taskRepository.countByPriority(Priority.HIGH)).build(),
                DashboardResponse.TaskByPriority.builder().priority("CRITICAL").count(taskRepository.countByPriority(Priority.CRITICAL)).build()
        );

        return DashboardResponse.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .totalUsers(totalUsers)
                .recentActivities(recentActivities)
                .tasksByStatus(tasksByStatus)
                .tasksByPriority(tasksByPriority)
                .build();
    }
}
