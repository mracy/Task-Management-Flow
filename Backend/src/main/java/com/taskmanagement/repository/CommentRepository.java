package com.taskmanagement.repository;

import com.taskmanagement.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    List<Comment> findByTaskIdAndDeletedFalseOrderByCreatedAtAsc(Long taskId);

    long countByTaskIdAndDeletedFalse(Long taskId);

    @Query("SELECT c FROM Comment c WHERE c.deleted = false AND c.task.id = :taskId ORDER BY c.createdAt DESC")
    Page<Comment> findByTaskId(@Param("taskId") Long taskId, Pageable pageable);
}
