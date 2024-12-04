package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.model.Participation;
import ru.practicum.ewm.model.dto.ParticipationRequestDto;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParticipationMapper {

    @Mapping(target = "requester", source = "participation.requester.id")
    @Mapping(target = "event", source = "participation.event.id")
    ParticipationRequestDto mapToDto(Participation participation);

}
