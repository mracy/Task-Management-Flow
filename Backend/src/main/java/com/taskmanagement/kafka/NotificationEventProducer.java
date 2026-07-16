package com.taskmanagement.kafka;

import com.taskmanagement.config.KafkaTopicConfig;
import com.taskmanagement.dto.event.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventProducer.class);
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationEventProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotificationEvent(NotificationEvent event) {
        log.info("Sending notification event: type={}, recipient={}", event.getType(), event.getRecipientId());
        kafkaTemplate.send(KafkaTopicConfig.NOTIFICATION_EVENTS_TOPIC, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send notification event: {}", ex.getMessage());
                    } else {
                        log.info("Notification event sent successfully");
                    }
                });
    }
}
