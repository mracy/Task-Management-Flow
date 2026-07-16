package com.taskmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to assign a task to a user")
public class AssignTaskRequest {

    @Schema(description = "ID of the user to assign the task to", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long assigneeId;
}
