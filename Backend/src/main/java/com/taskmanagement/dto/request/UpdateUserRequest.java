package com.taskmanagement.dto.request;

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
@Schema(description = "Request body to update user profile")
public class UpdateUserRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "Updated first name", example = "Jonathan")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "Updated last name", example = "Doe")
    private String lastName;

    @Schema(description = "Updated email", example = "jonathan.doe@example.com")
    private String email;
}
