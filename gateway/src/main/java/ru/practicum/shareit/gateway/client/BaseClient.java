package ru.practicum.shareit.gateway.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class BaseClient {

    protected final RestTemplate rest;
    private final String serverUrl;

    public BaseClient(RestTemplate rest, String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return get(path, userId, parameters, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters, @Nullable Object body) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> post(String path, long userId, Object body) {
        return post(path, (Long) userId, null, body);
    }

    protected ResponseEntity<Object> post(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> put(String path, long userId, Object body) {
        return put(path, (Long) userId, null, body);
    }

    protected ResponseEntity<Object> put(String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> patch(String path, long userId, Object body) {
        return patch(path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Long userId, @Nullable Map<String, Object> parameters, @Nullable Object body) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + path);
        if (parameters != null) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                builder.queryParam(parameter.getKey(), parameter.getValue());
            }
        }
        String url = builder.build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> shareitServerResponse;
        try {
            shareitServerResponse = rest.exchange(url, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsByteArray());
        }
        return shareitServerResponse;
    }
}

