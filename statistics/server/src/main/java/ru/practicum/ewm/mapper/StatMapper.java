package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;
import ru.practicum.ewm.model.Stat;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Stat paramHitDtoToStat(ParamHitDto paramHitDto);

    ParamHitDto statToParamHitDto(Stat stat);

    @Mapping(target = "hits", ignore = true)
    StatDto mapStatToStatDto(Stat stat);

}
