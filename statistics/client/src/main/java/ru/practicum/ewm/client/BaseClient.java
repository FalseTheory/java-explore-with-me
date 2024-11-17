package ru.practicum.ewm.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class BaseClient {

    protected final RestClient restClient;

    public BaseClient(RestClient restClient) {
        this.restClient = restClient;
    }

    protected List<?> get(String path, String start, String end, @Nullable List<String> uris, @Nullable Boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(path);
        builder.queryParam("start", start);
        builder.queryParam("end", end);
        if (uris != null) {
            builder.queryParam("uris", uris);
        }
        if (unique != null) {
            builder.queryParam("unique", unique);
        }
        return restClient.get()
                .uri(builder.build().encode(StandardCharsets.UTF_8).toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(List.class);

    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
