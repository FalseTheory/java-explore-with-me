package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
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
    public ParamHitDto hit(@RequestBody @Valid ParamHitDto paramHitDto) {
        log.info("hitting endpoint - {}", paramHitDto);
        ParamHitDto returnBody = service.hit(paramHitDto);
        log.info("statistic successfully updated");
        return returnBody;
    }

    @GetMapping("/stats")
    public List<StatDto> getAll(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam(required = false) String[] uris,
                                @RequestParam(defaultValue = "false") Boolean unique) {

        log.info("getting stats with params, start-{}, end -{}, uris - {}, unique - {}", start, end, uris, unique);
        List<StatDto> returnList = service.getAll(start, end, uris, unique);
        log.info("get request success");
        return returnList;
    }

}
