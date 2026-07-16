package com.taskmanagement.dto.response;

import com.taskmanagement.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSummaryResponse {

    private Long id;
    private String name;
    private ProjectStatus status;
}
