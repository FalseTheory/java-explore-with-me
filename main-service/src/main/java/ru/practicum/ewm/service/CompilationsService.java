package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationsService {

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto get(Long id);

    void delete(Long id);

    CompilationDto update(Long id, UpdateCompilationRequest updateBody);
}
