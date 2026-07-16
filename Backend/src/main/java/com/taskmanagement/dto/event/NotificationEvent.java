package com.taskmanagement.dto.event;

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
public class NotificationEvent {

    private String eventId;
    private NotificationType type;
    private String message;
    private Long recipientId;
    private Long entityId;
    private String entityType;
    private LocalDateTime timestamp;

    public static NotificationEvent of(NotificationType type, String message,
                                       Long recipientId, Long entityId, String entityType) {
        return NotificationEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .type(type)
                .message(message)
                .recipientId(recipientId)
                .entityId(entityId)
                .entityType(entityType)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
