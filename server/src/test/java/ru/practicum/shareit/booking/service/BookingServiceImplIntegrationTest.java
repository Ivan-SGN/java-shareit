package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void createBookingTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));
        User booker = userRepository.save(createUser("booker@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        BookingDto createdBooking =
                bookingService.create(booker.getId(), createBookingDto(item.getId()));

        Booking savedBooking = bookingRepository.findById(createdBooking.getId())
                .orElseThrow();

        assertThat(savedBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(savedBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void approveBookingTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));
        User booker = userRepository.save(createUser("booker@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        BookingDto createdBooking =
                bookingService.create(booker.getId(), createBookingDto(item.getId()));

        bookingService.approve(owner.getId(), createdBooking.getId(), true);

        Booking updatedBooking = bookingRepository.findById(createdBooking.getId())
                .orElseThrow();

        assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getBookingByIdTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));
        User booker = userRepository.save(createUser("booker@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        BookingDto createdBooking =
                bookingService.create(booker.getId(), createBookingDto(item.getId()));

        BookingDto foundBooking =
                bookingService.getById(booker.getId(), createdBooking.getId());

        assertThat(foundBooking.getId()).isEqualTo(createdBooking.getId());
        assertThat(foundBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getBookingsByUserTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));
        User booker = userRepository.save(createUser("booker@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        bookingService.create(booker.getId(), createBookingDto(item.getId()));

        List<BookingDto> bookings =
                bookingService.getByUser(booker.getId(), "ALL");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByOwnerTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));
        User booker = userRepository.save(createUser("booker@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        bookingService.create(booker.getId(), createBookingDto(item.getId()));

        List<BookingDto> bookings =
                bookingService.getByOwner(owner.getId(), "ALL");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void createBookingForOwnItemTest() {
        User owner = userRepository.save(createUser("owner@test.ru"));

        Item item = itemRepository.save(createItem(owner));

        assertThatThrownBy(() ->
                bookingService.create(owner.getId(), createBookingDto(item.getId())))
                .isInstanceOf(ValidationException.class);
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("Ivan");
        user.setEmail(email);
        return user;
    }

    private Item createItem(User owner) {
        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private BookingCreateDto createBookingDto(Long itemId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}