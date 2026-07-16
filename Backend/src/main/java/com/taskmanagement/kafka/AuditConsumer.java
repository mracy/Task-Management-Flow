package com.taskmanagement.kafka;

import com.taskmanagement.dto.event.AuditEvent;
import com.taskmanagement.entity.AuditLog;
import com.taskmanagement.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class AuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditConsumer.class);
    private final AuditLogRepository auditLogRepository;
    private final Set<String> processedEvents = new HashSet<>();

    public AuditConsumer(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @KafkaListener(topics = "audit-events", groupId = "audit-consumer-group")
    @Transactional
    public void consumeAuditEvent(AuditEvent event) {
        log.info("Received audit event: eventId={}, action={}, entity={}",
                event.getEventId(), event.getAction(), event.getEntityName());

        // Idempotency check
        if (processedEvents.contains(event.getEventId())) {
            log.warn("Duplicate audit event detected, skipping: {}", event.getEventId());
            return;
        }

        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(event.getAction())
                    .entityName(event.getEntityName())
                    .entityId(event.getEntityId())
                    .userId(event.getUserId())
                    .oldValues(event.getOldValues())
                    .newValues(event.getNewValues())
                    .build();

            auditLogRepository.save(auditLog);
            processedEvents.add(event.getEventId());

            // Prevent memory leak
            if (processedEvents.size() > 10000) {
                processedEvents.clear();
            }

            log.info("Audit log saved: id={}, action={}", auditLog.getId(), event.getAction());
        } catch (Exception e) {
            log.error("Error processing audit event: {}", e.getMessage(), e);
        }
    }
}
