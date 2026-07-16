package com.taskmanagement.kafka;

import com.taskmanagement.dto.event.NotificationEvent;
import com.taskmanagement.entity.Notification;
import com.taskmanagement.entity.User;
import com.taskmanagement.repository.NotificationRepository;
import com.taskmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final Set<String> processedEvents = new HashSet<>();

    public NotificationConsumer(NotificationRepository notificationRepository,
                                UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-consumer-group")
    @Transactional
    public void consumeNotificationEvent(NotificationEvent event) {
        log.info("Received notification event: eventId={}, type={}", event.getEventId(), event.getType());

        // Idempotency check
        if (processedEvents.contains(event.getEventId())) {
            log.warn("Duplicate notification event detected, skipping: {}", event.getEventId());
            return;
        }

        try {
            User recipient = userRepository.findById(event.getRecipientId())
                    .orElseThrow(() -> new RuntimeException("Recipient not found: " + event.getRecipientId()));

            Notification notification = Notification.builder()
                    .type(event.getType())
                    .message(event.getMessage())
                    .recipient(recipient)
                    .read(false)
                    .entityId(event.getEntityId())
                    .entityType(event.getEntityType())
                    .build();

            notificationRepository.save(notification);
            processedEvents.add(event.getEventId());

            // Prevent memory leak - keep only last 10000 event IDs
            if (processedEvents.size() > 10000) {
                processedEvents.clear();
            }

            log.info("Notification saved: id={}, recipient={}", notification.getId(), recipient.getEmail());
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
        }
    }
}
