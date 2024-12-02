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
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationsService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationsController {
    private final CompilationsService service;


    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("retrieving all compilations with parameters: pinned - {}", pinned);
        return service.getAll(pinned, PageRequest.of(from, size));
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto get(@PathVariable @Positive Long compId) {
        log.info("retrieving compilation with id - {}", compId);
        return service.get(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("creating new compilation with body - {}", newCompilationDto);
        CompilationDto created = service.create(newCompilationDto);
        log.info("created successfully");
        return created;
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        log.info("deleting compilation with id - {}", compId);
        service.delete(compId);
        log.info("deleted successfully");
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@PathVariable @Positive Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateBody) {
        log.info("updating compilation with id - {}, with body - {}", compId, updateBody);
        CompilationDto updated = service.update(compId, updateBody);
        log.info("updated successfully");
        return updated;

    }


}
