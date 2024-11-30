package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.model.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.model.util.AdminSearchParams;
import ru.practicum.ewm.model.util.SearchParams;
import ru.practicum.ewm.model.util.SortType;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.dto.event.NewEventDto;
import ru.practicum.ewm.service.EventsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventsController {
    private final EventsService service;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("user with id - {}, creating event - {}", userId, newEventDto);
        EventFullDto event = service.create(userId, newEventDto);
        log.info("event created");
        return event;
    }


    @GetMapping("/events")
    public List<EventFullDto> getAll(@RequestParam(required = false, defaultValue = "") String text,
                                     @RequestParam(required = false) Integer[] categories,
                                     @RequestParam(required = false) Boolean paid,
                                     @RequestParam(required = false) String rangeStart,
                                     @RequestParam(required = false) String rangeEnd,
                                     @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                     @RequestParam(required = false) SortType sortType,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     HttpServletRequest request) {
        SearchParams params = new SearchParams(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sortType, from, size);
        ParamHitDto paramHitDto = new ParamHitDto("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now().toString());
        log.info("retrieving all events with given parameters - {}", params);
        return service.getAll(params, paramHitDto);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAll(@RequestParam(required = false) Long[] users,
                                     @RequestParam(required = false) String[] states,
                                     @RequestParam(required = false) Long[] categories,
                                     @RequestParam(required = false) String rangeStart,
                                     @RequestParam(required = false) String rangeEnd,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        AdminSearchParams params = new AdminSearchParams(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("retrieving all events with given admin parameters - {}", params);
        return service.getAll(params);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto get(@PathVariable @Positive Long eventId,
                            HttpServletRequest request) {
        ParamHitDto paramHitDto = new ParamHitDto("ewm-main-service", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now().toString());
        log.info("retrieving event with id - {}", eventId);
        return service.get(eventId, paramHitDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getAll(@PathVariable @Positive Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("retrieving all events for user - {}", userId);
        return service.getAll(userId, PageRequest.of(from, size));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest updateBody) {
        updateBody.setEventId(eventId);
        updateBody.setUserId(userId);
        log.info("user - {} update of event - {}", userId, eventId);
        EventFullDto updated = service.update(updateBody);
        log.info("updated successfully");
        return updated;
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto publishEvent(@PathVariable @Positive Long eventId,
                                     @RequestBody @Valid UpdateEventAdminRequest updateBody) {
        log.info("publishing event - {} with body - {}", eventId, updateBody);
        EventFullDto published = service.publish(eventId, updateBody);
        log.info("operation successfull");
        return published;
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto get(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        log.info("user - {} retrieving information about event - {}", userId, eventId);
        return service.get(userId, eventId);
    }


}
