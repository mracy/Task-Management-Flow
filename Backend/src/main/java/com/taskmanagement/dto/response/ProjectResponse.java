package com.taskmanagement.dto.response;

import com.taskmanagement.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private UserSummaryResponse owner;
    private List<UserSummaryResponse> members;
    private Integer totalTasks;
    private Integer completedTasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
