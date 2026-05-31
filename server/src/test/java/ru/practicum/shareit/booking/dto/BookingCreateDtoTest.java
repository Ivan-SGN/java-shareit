package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void bookingCreateDtoSerializationTest() throws Exception {
        BookingCreateDto dto = getBookingCreateDto();

        JsonContent<BookingCreateDto> result =
                json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-01-01T10:00:00");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-01-02T10:00:00");
    }

    @Test
    void bookingCreateDtoDeserializationTest() throws Exception {
        BookingCreateDto sourceDto = getBookingCreateDto();

        BookingCreateDto parsedDto =
                json.parseObject(json.write(sourceDto).getJson());

        assertThat(parsedDto.getItemId())
                .isEqualTo(sourceDto.getItemId());

        assertThat(parsedDto.getStart())
                .isEqualTo(sourceDto.getStart());

        assertThat(parsedDto.getEnd())
                .isEqualTo(sourceDto.getEnd());
    }

    private BookingCreateDto getBookingCreateDto() {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));
        return dto;
    }
}