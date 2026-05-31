package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeBookingDtoTest() throws Exception {
        BookingDto bookingDto = createBookingDto();

        assertThat(json.write(bookingDto))
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(json.write(bookingDto))
                .extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");

        assertThat(json.write(bookingDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-05-30T14:00:00");

        assertThat(json.write(bookingDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-05-31T14:00:00");

        assertThat(json.write(bookingDto))
                .extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);

        assertThat(json.write(bookingDto))
                .extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(2);
    }

    private BookingDto createBookingDto() {
        Item item = new Item();
        item.setId(1L);

        User user = new User();
        user.setId(2L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItem(item);
        bookingDto.setBooker(user);
        bookingDto.setStart(LocalDateTime.of(2026, 5, 30, 14, 0));
        bookingDto.setEnd(LocalDateTime.of(2026, 5, 31, 14, 0));
        bookingDto.setStatus(BookingStatus.WAITING);

        return bookingDto;
    }
}