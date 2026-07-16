package com.taskmanagement.service;

import com.taskmanagement.dto.request.CreateCommentRequest;
import com.taskmanagement.dto.request.UpdateCommentRequest;
import com.taskmanagement.dto.response.CommentResponse;
import com.taskmanagement.dto.response.PaginatedResponse;

public interface CommentService {

    CommentResponse createComment(Long taskId, CreateCommentRequest request);

    PaginatedResponse<CommentResponse> getCommentsByTask(Long taskId, int page, int size);

    CommentResponse updateComment(Long commentId, UpdateCommentRequest request);

    void deleteComment(Long commentId);
}
