package com.taskmanagement.controller;

import com.taskmanagement.dto.response.DashboardResponse;
import com.taskmanagement.security.SecurityUtil;
import com.taskmanagement.service.DashboardService;
import com.taskmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user dashboard statistics")
    public ResponseEntity<DashboardResponse> getMyDashboard() {
        var currentUser = userService.getCurrentUserEntity();
        return ResponseEntity.ok(dashboardService.getDashboardStatsByUser(currentUser.getId()));
    }
}
