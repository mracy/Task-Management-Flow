package com.taskmanagement.mapper;

import com.taskmanagement.dto.request.CreateProjectRequest;
import com.taskmanagement.dto.request.UpdateProjectRequest;
import com.taskmanagement.dto.response.ProjectResponse;
import com.taskmanagement.dto.response.ProjectSummaryResponse;
import com.taskmanagement.entity.Project;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(target = "owner", source = "project.owner")
    @Mapping(target = "members", expression = "java(toSummaryList(project.getMembers()))")
    @Mapping(target = "totalTasks", expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    @Mapping(target = "completedTasks", expression = "java(getCompletedTaskCount(project))")
    ProjectResponse toResponse(Project project);

    @Mapping(target = "id", source = "id")
    ProjectSummaryResponse toSummaryResponse(Project project);

    List<ProjectSummaryResponse> toSummaryResponseList(List<Project> projects);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Project toEntity(CreateProjectRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateProjectRequest request, @MappingTarget Project project);

    default List<com.taskmanagement.dto.response.UserSummaryResponse> toSummaryList(Set<com.taskmanagement.entity.User> users) {
        if (users == null) return List.of();
        return users.stream().map(user -> {
            com.taskmanagement.dto.response.UserSummaryResponse summary = new com.taskmanagement.dto.response.UserSummaryResponse();
            summary.setId(user.getId());
            summary.setEmail(user.getEmail());
            summary.setFirstName(user.getFirstName());
            summary.setLastName(user.getLastName());
            summary.setFullName(user.getFullName());
            summary.setAvatarUrl(user.getAvatarUrl());
            return summary;
        }).collect(Collectors.toList());
    }

    default Integer getCompletedTaskCount(Project project) {
        if (project.getTasks() == null) return 0;
        return (int) project.getTasks().stream()
                .filter(t -> t.getStatus() == com.taskmanagement.enums.TaskStatus.DONE)
                .count();
    }
}
