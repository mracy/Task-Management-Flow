package com.taskmanagement.dto.request;

import com.taskmanagement.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to create a new project")
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 200, message = "Project name must not exceed 200 characters")
    @Schema(description = "Project name", example = "E-Commerce Platform", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Project description", example = "A full-stack e-commerce platform with payment integration, inventory management, and admin dashboard.")
    private String description;

    @Schema(description = "Project status", example = "PLANNING", allowableValues = {"PLANNING", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "ARCHIVED"})
    private ProjectStatus status;
}
