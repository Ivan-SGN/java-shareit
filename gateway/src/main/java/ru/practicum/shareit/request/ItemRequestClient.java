package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Component
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder
                .rootUri(serverUrl)
                .build());
    }

    public ResponseEntity<Object> create(Long userId, ItemRequestCreateDto dto) {
        return post(
                API_PREFIX,
                userId,
                dto
        );
    }

    public ResponseEntity<Object> getOwnRequests(Long userId) {
        return get(
                API_PREFIX,
                userId
        );
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get(
                API_PREFIX + "/all",
                userId
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(
                API_PREFIX + "/" + requestId,
                userId
        );
    }
}