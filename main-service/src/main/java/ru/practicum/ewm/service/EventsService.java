package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.model.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.model.util.AdminSearchParams;
import ru.practicum.ewm.model.util.SearchParams;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;

import java.util.List;

public interface EventsService {

    EventFullDto create(Long id, NewEventDto newEventDto);

    List<EventFullDto> getAll(SearchParams params, ParamHitDto paramHitDto);

    List<EventFullDto> getAll(AdminSearchParams params);

    List<EventFullDto> getAll(Long userId, Pageable pageable);


    EventFullDto get(Long id, ParamHitDto paramHitDto);

    EventFullDto get(Long userId, Long eventId);

    EventFullDto update(UpdateEventUserRequest updateBody);

    EventFullDto publish(Long eventId, UpdateEventAdminRequest updateBody);
}
