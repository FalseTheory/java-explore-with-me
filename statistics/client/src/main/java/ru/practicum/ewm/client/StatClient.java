package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.ParamHitDto;

import java.util.List;


@Component
public class StatClient extends BaseClient {
    final String statUrl;

    public StatClient(@Value("${ewm-server.url}") String statUrl) {
        super(RestClient.builder()
                .baseUrl(statUrl)
                .build());
        this.statUrl = statUrl;
    }

    public void hit(ParamHitDto paramHitDto) {
        post(statUrl + "/hit", paramHitDto);
    }

    public List<?> getStat(String start, String end, List<String> uris, Boolean unique) {
        return get(statUrl + "/stats", start, end, uris, unique);
    }

}
