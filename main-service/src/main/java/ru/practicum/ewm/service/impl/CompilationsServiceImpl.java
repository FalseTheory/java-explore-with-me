package ru.practicum.ewm.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.QCompilation;
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.exception.NotFoundException;
import ru.practicum.ewm.repository.CompilationsRepository;
import ru.practicum.ewm.repository.EventsRepository;
import ru.practicum.ewm.service.CompilationsService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationsServiceImpl implements CompilationsService {

    private final CompilationsRepository compilationsRepository;
    private final EventsRepository eventsRepository;
    private final CompilationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        BooleanExpression byPinned;
        if (pinned != null) {
            byPinned = QCompilation.compilation.pinned.in(pinned);
            return compilationsRepository.findAll(byPinned, pageable)
                    .stream()
                    .map(mapper::mapToCompilationDto).toList();
        } else {
            return compilationsRepository.findAll(pageable)
                    .stream()
                    .map(mapper::mapToCompilationDto).toList();
        }

    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> compilationEvents = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            compilationEvents = eventsRepository.findAllById(newCompilationDto.getEvents());
            if (compilationEvents.size() != newCompilationDto.getEvents().size()) {
                throw new NotFoundException("Some of the ids in your compilation are non-existent");
            }
        }
        Compilation compilation = compilationsRepository
                .save(mapper.mapNewToCompilation(newCompilationDto, compilationEvents));

        return mapper.mapToCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto get(Long id) {
        Compilation compilation = compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("compilation with id -" + id + " not found"));
        return mapper.mapToCompilationDto(compilation);
    }

    @Override
    public void delete(Long id) {
        compilationsRepository.deleteById(id);
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationRequest updateBody) {
        Compilation compilation = compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("compilation with id -" + id + " not found"));
        if (updateBody.getEvents() != null) {
            List<Event> updatedEvents = eventsRepository.findAllById(updateBody.getEvents());
            if (updatedEvents.size() != updateBody.getEvents().size()) {
                throw new NotFoundException("Some of the ids in your compilation are non-existent");
            }
            compilation.setEvents(updatedEvents);
        }
        if (updateBody.getPinned() != null) {
            compilation.setPinned(updateBody.getPinned());
        }
        if (updateBody.getTitle() != null) {
            compilation.setTitle(updateBody.getTitle());
        }


        return mapper.mapToCompilationDto(compilationsRepository.save(compilation));
    }
}
