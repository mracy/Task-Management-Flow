package com.taskmanagement.controller;

import com.taskmanagement.dto.request.CreateCommentRequest;
import com.taskmanagement.dto.request.UpdateCommentRequest;
import com.taskmanagement.dto.response.CommentResponse;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@Tag(name = "Comments", description = "Task comment management endpoints")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(summary = "Add a comment to a task")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long taskId,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(taskId, request));
    }

    @GetMapping
    @Operation(summary = "Get comments for a task")
    public ResponseEntity<PaginatedResponse<CommentResponse>> getComments(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId, page, size));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long taskId,
                                                         @PathVariable Long commentId,
                                                         @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long taskId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
