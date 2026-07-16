package com.taskmanagement.mapper;

import com.taskmanagement.dto.request.CreateCommentRequest;
import com.taskmanagement.dto.request.UpdateCommentRequest;
import com.taskmanagement.dto.response.CommentResponse;
import com.taskmanagement.entity.Comment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "author", source = "comment.author")
    @Mapping(target = "taskId", source = "comment.task.id")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment toEntity(CreateCommentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCommentRequest request, @MappingTarget Comment comment);
}
