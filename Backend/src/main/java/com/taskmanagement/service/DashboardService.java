package com.taskmanagement.service;

import com.taskmanagement.dto.response.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboardStats();

    DashboardResponse getDashboardStatsByUser(Long userId);
}
