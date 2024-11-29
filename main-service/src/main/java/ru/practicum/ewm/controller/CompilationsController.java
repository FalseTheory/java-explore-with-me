package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {

        return service.getAll(pinned, PageRequest.of(from, size));
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto get(@PathVariable @Positive Long compId) {
        return service.get(compId);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return service.create(newCompilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        service.delete(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@PathVariable @Positive Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateBody) {
        return service.update(compId, updateBody);

    }


}
