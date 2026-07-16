package com.taskmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to add a member to a project")
public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user to add as a project member", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
}
