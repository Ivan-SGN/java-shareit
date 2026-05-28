package ru.practicum.shareit.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class BaseClient {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    protected final RestTemplate restTemplate;

    protected ResponseEntity<Object> get(String path) {
        return sendRequest(
                HttpMethod.GET,
                path,
                null
        );
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return sendRequest(
                HttpMethod.GET,
                path,
                new HttpEntity<>(createHeaders(userId))
        );
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return sendRequest(
                HttpMethod.POST,
                path,
                new HttpEntity<>(body)
        );
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return sendRequest(
                HttpMethod.POST,
                path,
                new HttpEntity<>(body, createHeaders(userId))
        );
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return sendRequest(
                HttpMethod.PATCH,
                path,
                new HttpEntity<>(body, createHeaders(userId))
        );
    }

    protected ResponseEntity<Object> delete(String path) {
        return sendRequest(
                HttpMethod.DELETE,
                path,
                null
        );
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return sendRequest(
                HttpMethod.DELETE,
                path,
                new HttpEntity<>(createHeaders(userId))
        );
    }

    private ResponseEntity<Object> sendRequest(
            HttpMethod method,
            String path,
            HttpEntity<?> requestEntity
    ) {
        try {
            return restTemplate.exchange(
                    path,
                    method,
                    requestEntity,
                    Object.class
            );
        } catch (HttpStatusCodeException exception) {
            return ResponseEntity
                    .status(exception.getStatusCode())
                    .body(exception.getResponseBodyAsString());
        }
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER, String.valueOf(userId));

        return headers;
    }
}