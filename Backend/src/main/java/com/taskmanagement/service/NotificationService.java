package com.taskmanagement.service;

import com.taskmanagement.dto.response.NotificationResponse;
import com.taskmanagement.dto.response.PaginatedResponse;

public interface NotificationService {

    PaginatedResponse<NotificationResponse> getNotifications(int page, int size);

    NotificationResponse markAsRead(Long notificationId);

    void markAllAsRead();

    long getUnreadCount();
}
