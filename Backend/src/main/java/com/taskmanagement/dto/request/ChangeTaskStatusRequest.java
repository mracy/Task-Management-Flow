package com.taskmanagement.dto.request;

import com.taskmanagement.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to change task status")
public class ChangeTaskStatusRequest {

    @Schema(description = "New task status", example = "IN_PROGRESS", allowableValues = {"TODO", "IN_PROGRESS", "IN_REVIEW", "DONE", "CANCELLED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private TaskStatus status;
}
