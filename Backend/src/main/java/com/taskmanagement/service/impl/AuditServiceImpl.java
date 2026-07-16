package com.taskmanagement.service.impl;

import com.taskmanagement.dto.event.AuditEvent;
import com.taskmanagement.entity.AuditLog;
import com.taskmanagement.enums.AuditAction;
import com.taskmanagement.kafka.AuditEventProducer;
import com.taskmanagement.repository.AuditLogRepository;
import com.taskmanagement.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditLogRepository auditLogRepository;
    private final AuditEventProducer auditEventProducer;

    public AuditServiceImpl(AuditLogRepository auditLogRepository,
                            AuditEventProducer auditEventProducer) {
        this.auditLogRepository = auditLogRepository;
        this.auditEventProducer = auditEventProducer;
    }

    @Override
    @Transactional
    public void logAudit(AuditAction action, String entityName, String entityId,
                         Long userId, String oldValues, String newValues) {
        try {
            // Save directly to DB
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .userId(userId)
                    .oldValues(oldValues)
                    .newValues(newValues)
                    .build();
            auditLogRepository.save(auditLog);

            // Also publish to Kafka for async processing / external systems
            auditEventProducer.sendAuditEvent(
                    AuditEvent.of(action, entityName, entityId, userId, oldValues, newValues)
            );
        } catch (Exception e) {
            log.error("Error logging audit event: {}", e.getMessage(), e);
        }
    }
}
