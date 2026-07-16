package com.taskmanagement.kafka;

import com.taskmanagement.config.KafkaTopicConfig;
import com.taskmanagement.dto.event.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventProducer.class);
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskCreatedEvent(TaskEvent event) {
        log.info("Sending task-created event for task: {}", event.getTaskId());
        kafkaTemplate.send(KafkaTopicConfig.TASK_EVENTS_TOPIC, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send task-created event: {}", ex.getMessage());
                    } else {
                        log.info("Task-created event sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void sendTaskUpdatedEvent(TaskEvent event) {
        log.info("Sending task-updated event for task: {}", event.getTaskId());
        kafkaTemplate.send(KafkaTopicConfig.TASK_EVENTS_TOPIC, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send task-updated event: {}", ex.getMessage());
                    } else {
                        log.info("Task-updated event sent successfully");
                    }
                });
    }

    public void sendTaskAssignedEvent(TaskEvent event) {
        log.info("Sending task-assigned event for task: {}", event.getTaskId());
        kafkaTemplate.send(KafkaTopicConfig.TASK_EVENTS_TOPIC, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send task-assigned event: {}", ex.getMessage());
                    } else {
                        log.info("Task-assigned event sent successfully");
                    }
                });
    }
}
