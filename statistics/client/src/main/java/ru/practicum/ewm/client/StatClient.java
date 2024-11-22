package ru.practicum.ewm.client;

import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;

import java.util.List;

public interface StatClient {
    void hit(ParamHitDto paramHitDto);

    List<StatDto> getStat(String start, String end, List<String> uris, Boolean unique);
}
