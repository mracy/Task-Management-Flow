package com.taskmanagement.dto.request;

import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request body to create a new task")
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 300, message = "Title must not exceed 300 characters")
    @Schema(description = "Task title", example = "Implement user authentication", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Schema(description = "Task description", example = "Implement JWT-based authentication with access and refresh tokens, including password hashing with BCrypt.")
    private String description;

    @Schema(description = "Task priority", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
    private Priority priority;

    @Schema(description = "Initial task status", example = "TODO", allowableValues = {"TODO", "IN_PROGRESS", "IN_REVIEW", "DONE", "CANCELLED"})
    private TaskStatus status;

    @Schema(description = "Due date (ISO format)", example = "2026-08-15")
    private LocalDate dueDate;

    @Schema(description = "Assignee user ID", example = "3")
    private Long assigneeId;

    @NotNull(message = "Project ID is required")
    @Schema(description = "Project ID this task belongs to", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long projectId;

    @Schema(description = "Sort order within the project", example = "0")
    private Integer orderIndex;
}
