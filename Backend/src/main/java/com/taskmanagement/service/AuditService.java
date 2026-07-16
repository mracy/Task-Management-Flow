package com.taskmanagement.service;

import com.taskmanagement.enums.AuditAction;

public interface AuditService {

    void logAudit(AuditAction action, String entityName, String entityId,
                  Long userId, String oldValues, String newValues);
}
