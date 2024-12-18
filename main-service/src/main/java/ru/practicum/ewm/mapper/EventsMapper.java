package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.dto.comment.CommentShortDto;
import ru.practicum.ewm.model.util.Location;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventsMapper {

    DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "lat", source = "newEventDto.location.lat")
    @Mapping(target = "lon", source = "newEventDto.location.lon")
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventDate", expression = "java(stringToLocalDateTime(newEventDto))")
    @Mapping(target = "createdOn", expression = "java(lockCreatedOn())")
    @Mapping(target = "participationLimit", source = "newEventDto.participantLimit")
    Event mapNewToEvent(User user, Category category, NewEventDto newEventDto);

    @Mapping(target = "location", expression = "java(getLocation(event))")
    @Mapping(target = "initiator", expression = "java(userToUserShortDto(event))")
    @Mapping(target = "eventDate", expression = "java(formatTime(event))")
    @Mapping(target = "participantLimit", source = ("event.participationLimit"))
    @Mapping(target = "eventComments", expression = "java(commentListToCommentShortDtoList(event))")
    EventFullDto mapEventToFullDto(Event event);

    default Location getLocation(Event event) {
        return new Location(event.getLat(), event.getLon());
    }

    default UserShortDto userToUserShortDto(Event event) {
        return new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());
    }

    default UserShortDto userToUserShortDto(Comment comment) {
        return new UserShortDto(comment.getAuthor().getId(), comment.getAuthor().getName());
    }

    default LocalDateTime stringToLocalDateTime(NewEventDto eventDto) {

        return LocalDateTime.parse(eventDto.getEventDate(), datePattern);
    }

    default LocalDateTime lockCreatedOn() {
        return LocalDateTime.now();
    }

    default String formatTime(Event event) {
        LocalDateTime localDateTime = event.getEventDate();
        return localDateTime.format(datePattern);
    }

    default CommentShortDto commentToCommentShortDto(Comment comment, Long eventId) {
        if (comment == null) {
            return null;
        }

        CommentShortDto commentShortDto = new CommentShortDto();

        commentShortDto.setAuthor(userToUserShortDto(comment));
        commentShortDto.setText(comment.getText());
        commentShortDto.setPublishedAt(comment.getPublishedAt());
        commentShortDto.setEventId(eventId);

        return commentShortDto;
    }

    default List<CommentShortDto> commentListToCommentShortDtoList(Event event) {
        if (event.getEventComments() == null) {
            return null;
        }

        List<CommentShortDto> shortDtoCommentsList = new ArrayList<>(event.getEventComments().size());
        for (Comment comment : event.getEventComments()) {
            shortDtoCommentsList.add(commentToCommentShortDto(comment, event.getId()));
        }

        return shortDtoCommentsList;
    }


}
