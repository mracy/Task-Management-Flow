package com.taskmanagement.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEvent {

    private String eventId;
    private String eventType;
    private Long taskId;
    private String taskTitle;
    private Long projectId;
    private Long assigneeId;
    private Long reporterId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime timestamp;

    public static TaskEvent of(String eventType, Long taskId, String taskTitle,
                               Long projectId, Long assigneeId, Long reporterId,
                               String oldStatus, String newStatus) {
        return TaskEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(eventType)
                .taskId(taskId)
                .taskTitle(taskTitle)
                .projectId(projectId)
                .assigneeId(assigneeId)
                .reporterId(reporterId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
