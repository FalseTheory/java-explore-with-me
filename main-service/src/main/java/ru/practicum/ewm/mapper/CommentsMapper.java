package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.comment.CommentDto;
import ru.practicum.ewm.model.dto.comment.CommentShortDto;
import ru.practicum.ewm.model.dto.comment.NewCommentDto;
import ru.practicum.ewm.model.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentsMapper {

    @Mapping(target = "event", source = "event")
    @Mapping(target = "author", source = "user")
    @Mapping(target = "publishedAt", expression = "java(lockCreatedOn())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "edited", expression = "java(getEditedDefault())")
    Comment mapNewToComment(NewCommentDto newCommentDto, Event event, User user);

    @Mapping(target = "author", expression = "java(userToUserShortDto(comment))")
    CommentDto mapToDto(Comment comment);

    @Mapping(target = "author", expression = "java(userToUserShortDto(comment))")
    @Mapping(target = "eventId", source = "comment.event.id")
    @Mapping(target = "edited", expression = "java(getEditedDefault())")
    CommentShortDto mapToShortDto(Comment comment);

    default LocalDateTime lockCreatedOn() {
        return LocalDateTime.now();
    }

    default UserShortDto userToUserShortDto(Comment comment) {
        return new UserShortDto(comment.getAuthor().getId(), comment.getAuthor().getName());
    }

    default Boolean getEditedDefault() {
        return false;
    }
}
