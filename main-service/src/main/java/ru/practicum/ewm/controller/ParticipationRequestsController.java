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
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable @Positive Long userId) {
        log.info("retrieving all participation requests for user - {}", userId);
        return service.getAllForUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto create(@PathVariable @Positive Long userId,
                                          @RequestParam @Positive Long eventId) {
        log.info("user - {} trying to create participation request for event {}", userId, eventId);
        ParticipationRequestDto created = service.create(userId, eventId);
        log.info("request created successfully");
        return created;
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long requestId) {
        log.info("user - {} canceling his participation request - {}", userId, requestId);
        ParticipationRequestDto canceled = service.cancel(userId, requestId);
        log.info("canceled successfully");
        return canceled;
    }

    @GetMapping("users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventsRequests(@PathVariable @Positive Long userId,
                                                               @PathVariable @Positive Long eventId) {
        log.info("get all request for user - {} event - {}", userId, eventId);
        return service.getUserEventsRequests(userId, eventId);
    }

    @PatchMapping("users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequests(@PathVariable @Positive Long userId,
                                                                  @PathVariable @Positive Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest updateBody) {
        updateBody.setEventId(eventId);
        updateBody.setUserId(userId);
        log.info("user - {} updating request status for his event - {} with updateBody - {}", userId, eventId, updateBody);
        EventRequestStatusUpdateResult result = service.updateUserEventsRequests(updateBody);
        log.info("statuses updated successfully");
        return result;
    }
}
