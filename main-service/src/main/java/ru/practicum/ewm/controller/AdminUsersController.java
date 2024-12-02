package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.user.NewUserRequest;
import ru.practicum.ewm.model.dto.user.UserDto;
import ru.practicum.ewm.service.UsersService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class AdminUsersController {
    private final UsersService service;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) int[] ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("retrieveing all users (admin endpoint");
        return service.getAllFiltered(ids, PageRequest.of(from, size));

    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @Positive Long userId) {
        log.info("deleting user with id - {}", userId);
        service.delete(userId);
        log.info("user with id - {} has been deleted", userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUser) {
        log.info("creating user with body - {}", newUser);
        UserDto created = service.create(newUser);
        log.info("created successfully");
        return created;
    }

}
