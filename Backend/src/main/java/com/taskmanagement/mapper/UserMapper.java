package com.taskmanagement.mapper;

import com.taskmanagement.dto.request.RegisterRequest;
import com.taskmanagement.dto.request.UpdateUserRequest;
import com.taskmanagement.dto.response.UserResponse;
import com.taskmanagement.dto.response.UserSummaryResponse;
import com.taskmanagement.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "id", source = "id")
    UserResponse toResponse(User user);

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserSummaryResponse toSummaryResponse(User user);

    List<UserSummaryResponse> toSummaryResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "reportedTasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User toEntity(RegisterRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
