package com.taskmanagement.repository;

import com.taskmanagement.entity.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByDeletedFalse(Pageable pageable);

    Page<Task> findByProjectIdAndDeletedFalse(Long projectId, Pageable pageable);

    Page<Task> findByStatusAndDeletedFalse(TaskStatus status, Pageable pageable);

    Page<Task> findByPriorityAndDeletedFalse(Priority priority, Pageable pageable);

    Page<Task> findByAssigneeIdAndDeletedFalse(Long assigneeId, Pageable pageable);

    Page<Task> findByReporterIdAndDeletedFalse(Long reporterId, Pageable pageable);

    Page<Task> findByProjectIdAndStatusAndDeletedFalse(Long projectId, TaskStatus status, Pageable pageable);

    Page<Task> findByProjectIdAndPriorityAndDeletedFalse(Long projectId, Priority priority, Pageable pageable);

    Page<Task> findByAssigneeIdAndStatusAndDeletedFalse(Long assigneeId, TaskStatus status, Pageable pageable);

    List<Task> findByProjectIdAndDeletedFalseOrderByOrderIndexAsc(Long projectId);

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Task> searchTasks(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND t.dueDate < :date AND t.status != 'DONE' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksDueSoon(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deleted = false")
    long countActiveTasks();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deleted = false AND t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deleted = false AND t.priority = :priority")
    long countByPriority(@Param("priority") Priority priority);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deleted = false AND t.dueDate < :date AND t.status != 'DONE' AND t.status != 'CANCELLED'")
    long countOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND t.project.id = :projectId AND t.assignee.id = :assigneeId")
    Page<Task> findByProjectIdAndAssigneeIdAndDeletedFalse(@Param("projectId") Long projectId,
                                                           @Param("assigneeId") Long assigneeId,
                                                           Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.deleted = false AND t.dueDate BETWEEN :start AND :end AND t.project.id = :projectId")
    List<Task> findTasksByProjectAndDueDateRange(@Param("projectId") Long projectId,
                                                  @Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);
}
