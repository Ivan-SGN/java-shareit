package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(
            @Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder
                .rootUri(serverUrl)
                .build());
    }

    public ResponseEntity<Object> create(Long userId, BookingCreateDto dto) {
        return post(
                API_PREFIX,
                userId,
                dto
        );
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        return patch(
                API_PREFIX + "/" + bookingId + "?approved=" + approved,
                userId,
                null
        );
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(
                API_PREFIX + "/" + bookingId,
                userId
        );
    }

    public ResponseEntity<Object> getByUser(Long userId, String state) {
        return get(
                API_PREFIX + "?state=" + state,
                userId
        );
    }

    public ResponseEntity<Object> getByOwner(Long userId, String state) {
        return get(
                API_PREFIX + "/owner?state=" + state,
                userId
        );
    }
}