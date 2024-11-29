package ru.practicum.ewm.service;

import ru.practicum.ewm.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateResult;

import java.util.List;

public interface ParticipationsService {

    List<ParticipationRequestDto> getAllForUser(Long id);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancel(Long userId, Long requestId);


    List<ParticipationRequestDto> getUserEventsRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUserEventsRequests(EventRequestStatusUpdateRequest updateRequest);
}
