package com.taskmanagement.service.impl;

import com.taskmanagement.dto.event.NotificationEvent;
import com.taskmanagement.dto.request.CreateCommentRequest;
import com.taskmanagement.dto.request.UpdateCommentRequest;
import com.taskmanagement.dto.response.CommentResponse;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.entity.Comment;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.User;
import com.taskmanagement.enums.NotificationType;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.exception.ForbiddenException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.kafka.NotificationEventProducer;
import com.taskmanagement.mapper.CommentMapper;
import com.taskmanagement.repository.CommentRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.CommentService;
import com.taskmanagement.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final NotificationEventProducer notificationEventProducer;

    public CommentServiceImpl(CommentRepository commentRepository,
                              TaskRepository taskRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper,
                              NotificationEventProducer notificationEventProducer) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(currentUser)
                .task(task)
                .build();

        comment = commentRepository.save(comment);

        // Notify task assignee if the comment author is not the assignee
        if (task.getAssignee() != null && !task.getAssignee().getId().equals(currentUser.getId())) {
            notificationEventProducer.sendNotificationEvent(
                    NotificationEvent.of(
                            NotificationType.COMMENT_ADDED,
                            currentUser.getFullName() + " commented on task: " + task.getTitle(),
                            task.getAssignee().getId(), taskId, "Task"
                    )
            );
        }

        // Notify reporter if the comment author is not the reporter
        if (!task.getReporter().getId().equals(currentUser.getId())) {
            notificationEventProducer.sendNotificationEvent(
                    NotificationEvent.of(
                            NotificationType.COMMENT_ADDED,
                            currentUser.getFullName() + " commented on task: " + task.getTitle(),
                            task.getReporter().getId(), taskId, "Task"
                    )
            );
        }

        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<CommentResponse> getCommentsByTask(Long taskId, int page, int size) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> comments = commentRepository.findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(taskId, pageable);
        return PaginationUtil.toPaginatedResponse(comments.map(commentMapper::toResponse));
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        User currentUser = getCurrentUser();

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the comment author can edit the comment");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        User currentUser = getCurrentUser();

        if (!comment.getAuthor().getId().equals(currentUser.getId()) && !SecurityUtil.isAdmin()) {
            throw new ForbiddenException("Only the comment author or admin can delete the comment");
        }

        commentRepository.delete(comment);
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Current user not found"));
    }
}
