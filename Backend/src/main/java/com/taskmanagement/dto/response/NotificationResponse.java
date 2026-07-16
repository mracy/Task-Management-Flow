package com.taskmanagement.dto.response;

import com.taskmanagement.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String message;
    private Boolean read;
    private LocalDateTime createdAt;
    private Long entityId;
    private String entityType;
}
