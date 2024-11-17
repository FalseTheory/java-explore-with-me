package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;
import ru.practicum.ewm.service.StatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ParamHitDto hit(@RequestBody ParamHitDto paramHitDto) {
        log.info("hitting endpoint - {}", paramHitDto);
        ParamHitDto returnBody = service.hit(paramHitDto);
        log.info("statistic successfully updated");
        return returnBody;
    }

    @GetMapping("/stats")
    public List<StatDto> getAll(@RequestParam(required = true) String start,
                                @RequestParam(required = true) String end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        return service.getAll(start, end, uris, unique);
    }

}
