package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingCreateDto dto
    ) {
        if (!dto.getEnd().isAfter(dto.getStart()) || dto.getStart().isEqual(dto.getEnd())) {
            throw new IllegalArgumentException("Invalid booking time");
        }
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId,
                                          @RequestParam("approved") boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingClient.getByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingClient.getByOwner(userId, state);
    }
}