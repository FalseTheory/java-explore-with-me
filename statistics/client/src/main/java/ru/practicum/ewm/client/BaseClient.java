package ru.practicum.ewm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseClient<T> {

    protected final RestClient restClient;

    public BaseClient(RestClient restClient) {
        this.restClient = restClient;
    }

    protected List<T> get(String path, String start, String end, @Nullable List<String> uris, @Nullable Boolean unique) {
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
                .body(new ParameterizedTypeReference<>() {
                });

    }

    protected <X> ResponseEntity<Void> post(String path, X body) {
        return restClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
