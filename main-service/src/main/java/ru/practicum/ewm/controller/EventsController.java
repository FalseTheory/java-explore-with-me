package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.EventsService;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventsController {
    private final EventsService service;
}
