package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;

import java.util.List;

public interface StatService {

    ParamHitDto hit(ParamHitDto paramHitDto);

    List<StatDto> getAll(String start, String end, String[] uris, Boolean unique);
}
