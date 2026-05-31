package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void bookingNotFoundTest() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.getById(1L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void userNotFoundTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.getByUser(1L, "ALL"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void itemNotFoundTest() {
        User user = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                bookingService.create(1L, createBookingDto(1L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Item not found");
    }

    @Test
    void approveByNonOwnerTest() {
        Booking booking = createBooking(10L, 20L);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() ->
                bookingService.approve(999L, 1L, true))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void approveAlreadyProcessedTest() {
        Booking booking = createBooking(10L, 20L);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() ->
                bookingService.approve(10L, 1L, true))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("already processed");
    }

    @Test
    void createBookingForUnavailableItemTest() {
        User booker = createUser(1L);

        Item item = createItem(2L);
        item.setAvailable(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                bookingService.create(1L, createBookingDto(1L)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void createBookingWithEqualDatesTest() {
        User booker = createUser(1L);

        Item item = createItem(2L);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(dto.getStart());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                bookingService.create(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid booking time");
    }

    @Test
    void createBookingWithStartAfterEndTest() {
        User booker = createUser(1L);

        Item item = createItem(2L);

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(booker));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                bookingService.create(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid booking time");
    }

    @Test
    void getBookingAccessDeniedTest() {
        Booking booking = createBooking(10L, 20L);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        assertThatThrownBy(() ->
                bookingService.getById(999L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void createBookingByOwnerTest() {
        User owner = createUser(1L);
        Item item = createItem(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                bookingService.create(1L, createBookingDto(1L)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Owner cannot book own item");
    }

    @Test
    void getBookingsByUserInvalidStateTest() {
        User user = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() ->
                bookingService.getByUser(1L, "INVALID"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Unknown state");
    }

    @Test
    void getBookingsByUserFutureStateTest() {
        User user = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        bookingService.getByUser(1L, "FUTURE");
    }

    @Test
    void getBookingsByUserWaitingStateTest() {
        User user = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                1L,
                BookingStatus.WAITING))
                .thenReturn(java.util.List.of());

        bookingService.getByUser(1L, "WAITING");
    }

    @Test
    void getBookingsByUserRejectedStateTest() {
        User user = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                1L,
                BookingStatus.REJECTED))
                .thenReturn(java.util.List.of());

        bookingService.getByUser(1L, "REJECTED");
    }

    @Test
    void getBookingsByOwnerCurrentStateTest() {
        User owner = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        bookingService.getByOwner(1L, "CURRENT");
    }

    @Test
    void getBookingsByOwnerPastStateTest() {
        User owner = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        bookingService.getByOwner(1L, "PAST");
    }

    @Test
    void getBookingsByOwnerFutureStateTest() {
        User owner = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.List.of());

        bookingService.getByOwner(1L, "FUTURE");
    }

    @Test
    void getBookingsByOwnerWaitingStateTest() {
        User owner = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                1L,
                BookingStatus.WAITING))
                .thenReturn(java.util.List.of());

        bookingService.getByOwner(1L, "WAITING");
    }

    @Test
    void getBookingsByOwnerRejectedStateTest() {
        User owner = createUser(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                1L,
                BookingStatus.REJECTED))
                .thenReturn(java.util.List.of());

        bookingService.getByOwner(1L, "REJECTED");
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Item createItem(Long ownerId) {
        User owner = createUser(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);

        return item;
    }

    private Booking createBooking(Long ownerId, Long bookerId) {
        Item item = createItem(ownerId);
        User booker = createUser(bookerId);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    private BookingCreateDto createBookingDto(Long itemId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        return dto;
    }
}