package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingMapperTest {

    private final BookingMapper bookingMapper;

    @Test
    void bookingCreateDtoToEntityTest() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));

        Booking booking =
                bookingMapper.toEntity(dto, item, booker);

        assertThat(booking.getId()).isNull();

        assertThat(booking.getStart())
                .isEqualTo(dto.getStart());

        assertThat(booking.getEnd())
                .isEqualTo(dto.getEnd());

        assertThat(booking.getItem())
                .isEqualTo(item);

        assertThat(booking.getBooker())
                .isEqualTo(booker);

        assertThat(booking.getStatus()).isNull();
    }

    @Test
    void bookingToDtoTest() {
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(10L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.of(2026, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 1, 2, 10, 0));
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto dto =
                bookingMapper.toDto(booking);

        assertThat(dto.getId())
                .isEqualTo(booking.getId());

        assertThat(dto.getItem())
                .isEqualTo(item);

        assertThat(dto.getBooker())
                .isEqualTo(booker);

        assertThat(dto.getStart())
                .isEqualTo(booking.getStart());

        assertThat(dto.getEnd())
                .isEqualTo(booking.getEnd());

        assertThat(dto.getStatus())
                .isEqualTo(booking.getStatus());
    }
}