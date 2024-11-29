package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.util.Location;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.model.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventsMapper {


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
    EventFullDto mapEventToFullDto(Event event);

    default Location getLocation(Event event) {
        return new Location(event.getLat(), event.getLon());
    }

    default UserShortDto userToUserShortDto(Event event) {
        return new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());
    }

    default LocalDateTime stringToLocalDateTime(NewEventDto eventDto) {
        final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return LocalDateTime.parse(eventDto.getEventDate(), datePattern);
    }

    default LocalDateTime lockCreatedOn() {
        return LocalDateTime.now();
    }

    default String formatTime(Event event) {
        LocalDateTime localDateTime = event.getEventDate();
        final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(datePattern);
    }


}
