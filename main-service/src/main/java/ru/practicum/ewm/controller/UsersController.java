package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.service.UsersService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {
    private final UsersService service;
}
