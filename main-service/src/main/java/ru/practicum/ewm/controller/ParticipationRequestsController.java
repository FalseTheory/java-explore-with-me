package ru.practicum.ewm.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.ParticipationsService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class ParticipationRequestsController {
    private final ParticipationsService service;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable Long userId) {
        return service.getAllForUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        return service.create(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long requestId) {
        return service.cancel(userId, requestId);
    }

    @GetMapping("users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventsRequests(@PathVariable @Positive Long userId,
                                                               @PathVariable @Positive Long eventId) {
        return service.getUserEventsRequests(userId, eventId);
    }

    @PatchMapping("users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequests(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest updateBody) {
        updateBody.setEventId(eventId);
        updateBody.setUserId(userId);
        return service.updateUserEventsRequests(updateBody);
    }
}
