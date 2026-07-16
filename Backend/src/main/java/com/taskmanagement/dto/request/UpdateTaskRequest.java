package com.taskmanagement.dto.request;

import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to update a task")
public class UpdateTaskRequest {

    @Size(max = 300, message = "Title must not exceed 300 characters")
    @Schema(description = "Updated task title", example = "Implement JWT authentication")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Schema(description = "Updated description", example = "JWT authentication with refresh tokens and role-based access control.")
    private String description;

    @Schema(description = "Updated priority", example = "CRITICAL", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
    private Priority priority;

    @Schema(description = "Updated status", example = "IN_PROGRESS", allowableValues = {"TODO", "IN_PROGRESS", "IN_REVIEW", "DONE", "CANCELLED"})
    private TaskStatus status;

    @Schema(description = "Updated due date", example = "2026-08-20")
    private LocalDate dueDate;

    @Schema(description = "Reassign to user ID", example = "2")
    private Long assigneeId;

    @Schema(description = "Updated sort order", example = "1")
    private Integer orderIndex;
}
