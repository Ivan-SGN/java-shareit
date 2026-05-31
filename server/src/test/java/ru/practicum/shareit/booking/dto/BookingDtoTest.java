package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
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
    void bookingDtoSerializationTest() throws Exception {
        BookingDto dto = createBookingDto();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-05-30T14:00:00");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-05-31T14:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(2);
    }

    @Test
    void bookingDtoDeserializationTest() throws Exception {
        BookingDto sourceDto = createBookingDto();

        BookingDto parsedDto =
                json.parseObject(json.write(sourceDto).getJson());

        assertThat(parsedDto.getId())
                .isEqualTo(sourceDto.getId());

        assertThat(parsedDto.getStatus())
                .isEqualTo(sourceDto.getStatus());

        assertThat(parsedDto.getStart())
                .isEqualTo(sourceDto.getStart());

        assertThat(parsedDto.getEnd())
                .isEqualTo(sourceDto.getEnd());

        assertThat(parsedDto.getItem().getId())
                .isEqualTo(sourceDto.getItem().getId());

        assertThat(parsedDto.getBooker().getId())
                .isEqualTo(sourceDto.getBooker().getId());
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