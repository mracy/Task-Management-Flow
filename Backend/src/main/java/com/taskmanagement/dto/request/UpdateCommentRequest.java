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
@Schema(description = "Request body to update an existing comment")
public class UpdateCommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(max = 5000, message = "Comment must not exceed 5000 characters")
    @Schema(description = "Updated comment text", example = "Updated: I've reviewed the authentication flow. Added rate limiting suggestion.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
