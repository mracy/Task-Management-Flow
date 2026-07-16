package com.taskmanagement.service.impl;

import com.taskmanagement.dto.response.NotificationResponse;
import com.taskmanagement.dto.response.PaginatedResponse;
import com.taskmanagement.entity.User;
import com.taskmanagement.exception.BadRequestException;
import com.taskmanagement.exception.ResourceNotFoundException;
import com.taskmanagement.repository.NotificationRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.NotificationService;
import com.taskmanagement.mapper.NotificationMapper;
import com.taskmanagement.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<NotificationResponse> getNotifications(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<com.taskmanagement.entity.Notification> notifications =
                notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        return PaginationUtil.toPaginatedResponse(notifications.map(notificationMapper::toResponse));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();
        com.taskmanagement.entity.Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Notification", notificationId));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Notification does not belong to current user");
        }

        notification.setRead(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        notificationRepository.markAllAsRead(currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByRecipientIdAndReadFalse(currentUser.getId());
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Current user not found"));
    }
}
