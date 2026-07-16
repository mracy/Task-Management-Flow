package com.taskmanagement.dto.request;

import com.taskmanagement.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to update a project")
public class UpdateProjectRequest {

    @Size(max = 200, message = "Project name must not exceed 200 characters")
    @Schema(description = "Updated project name", example = "E-Commerce Platform v2")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Updated description", example = "Updated scope: added multi-tenant support and analytics.")
    private String description;

    @Schema(description = "Updated project status", example = "IN_PROGRESS", allowableValues = {"PLANNING", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "ARCHIVED"})
    private ProjectStatus status;
}
