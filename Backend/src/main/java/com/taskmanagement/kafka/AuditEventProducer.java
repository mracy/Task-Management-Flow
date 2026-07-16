package com.taskmanagement.kafka;

import com.taskmanagement.config.KafkaTopicConfig;
import com.taskmanagement.dto.event.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AuditEventProducer.class);
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    public AuditEventProducer(KafkaTemplate<String, AuditEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuditEvent(AuditEvent event) {
        log.info("Sending audit event: action={}, entity={}", event.getAction(), event.getEntityName());
        kafkaTemplate.send(KafkaTopicConfig.AUDIT_EVENTS_TOPIC, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send audit event: {}", ex.getMessage());
                    } else {
                        log.info("Audit event sent successfully");
                    }
                });
    }
}
