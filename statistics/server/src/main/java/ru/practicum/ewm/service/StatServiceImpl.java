package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParamHitDto;
import ru.practicum.ewm.dto.StatDto;
import ru.practicum.ewm.mapper.StatMapper;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.model.exception.BadParametersException;
import ru.practicum.ewm.repository.StatRepository;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.net.URLDecoder;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository repository;
    private final StatMapper mapper;

    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public ParamHitDto hit(ParamHitDto paramHitDto) {
        Stat stat = mapper.paramHitDtoToStat(paramHitDto);
        stat.setTimestamp(Timestamp.from(Instant.now()));

        return mapper.statToParamHitDto(repository.save(stat));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatDto> getAll(String start, String end, String[] uris, Boolean unique) {


        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), datePattern);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), datePattern);

        if (startTime.isAfter(endTime)) {
            throw new BadParametersException("wrong dates");
        }
        if (uris == null && !unique) {
            return mapOccurrences(repository.findByDate(startTime, endTime).stream()
                    .map(mapper::mapStatToStatDto).toList());
        } else if (uris == null) {
            return mapOccurrences(repository.findByDate(startTime, endTime).stream()
                    .distinct().map(mapper::mapStatToStatDto).toList());
        } else if (!unique) {
            return mapOccurrences(repository.findByDateAndUri(startTime, endTime, uris).stream()
                    .map(mapper::mapStatToStatDto).toList());
        } else {
            return mapOccurrences(repository.findByDateAndUri(startTime, endTime, uris).stream()
                    .distinct().map(mapper::mapStatToStatDto).toList());
        }
    }

    private List<StatDto> mapOccurrences(List<StatDto> statDtoList) {
        Map<StatDto, Long> occurencyMap = statDtoList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Set<StatDto> returnSet = new HashSet<>(statDtoList);
        returnSet.forEach(statDto -> statDto.setHits(occurencyMap.get(statDto)));

        return returnSet.stream().sorted((o1, o2) -> Long.compare(o2.getHits(), o1.getHits())).toList();
    }

}




