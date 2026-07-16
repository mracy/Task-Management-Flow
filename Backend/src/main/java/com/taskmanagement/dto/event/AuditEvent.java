package com.taskmanagement.dto.event;

import com.taskmanagement.enums.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    private String eventId;
    private AuditAction action;
    private String entityName;
    private String entityId;
    private Long userId;
    private String oldValues;
    private String newValues;
    private LocalDateTime timestamp;

    public static AuditEvent of(AuditAction action, String entityName, String entityId,
                                Long userId, String oldValues, String newValues) {
        return AuditEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .userId(userId)
                .oldValues(oldValues)
                .newValues(newValues)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
