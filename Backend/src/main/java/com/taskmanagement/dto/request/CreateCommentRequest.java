package com.taskmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body to add a comment to a task")
public class CreateCommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    @Schema(description = "Comment text", example = "I've reviewed the authentication flow. The JWT implementation looks solid, but we should add rate limiting to the login endpoint.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
