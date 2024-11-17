package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.ParamHitDto;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;


@Component
@Slf4j
public class StatClient extends BaseClient {
    final String statUrl;

    public StatClient(@Value("${ewm-server.url}") String statUrl) {
        super(RestClient.builder()
                .baseUrl(statUrl)
                .build());
        this.statUrl = statUrl;
    }

    public void hit(ParamHitDto paramHitDto) {
        try{
            post(statUrl + "/hit", paramHitDto);
        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            log.error("Error on statistics app occurred. \nReason - {} \nTrace - {}", e.getMessage(), stackTrace);
        }

    }

    public List<?> getStat(String start, String end, List<String> uris, Boolean unique) {
        try {
            return get(statUrl + "/stats", start, end, uris, unique);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            log.error("Error on statistics app occurred. \nReason - {} \nTrace - {}", e.getMessage(), stackTrace);

            return Collections.emptyList();
        }
    }

}
