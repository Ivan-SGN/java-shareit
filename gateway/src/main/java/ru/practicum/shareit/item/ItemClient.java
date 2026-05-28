package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Component
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    public ItemClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder
                .rootUri(serverUrl)
                .build());
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto dto) {
        return post(
                API_PREFIX,
                userId,
                dto
        );
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto dto) {
        return patch(
                API_PREFIX + "/" + itemId,
                userId,
                dto
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long itemId) {
        return get(
                API_PREFIX + "/" + itemId,
                userId
        );
    }

    public ResponseEntity<Object> getByOwner(Long userId) {
        return get(
                API_PREFIX,
                userId
        );
    }

    public ResponseEntity<Object> search(String text) {
        return get(
                API_PREFIX + "/search?text=" + text
        );
    }

    public ResponseEntity<Object> createComment(
            Long userId,
            Long itemId,
            CommentCreateDto dto
    ) {
        return post(
                API_PREFIX + "/" + itemId + "/comment",
                userId,
                dto
        );
    }
}