package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentMapper commentMapper;

    @Spy
    private ItemMapper itemMapper = new ItemMapperImpl();

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void searchEmptyTextTest() {
        assertThat(itemService.search(""))
                .isEmpty();
    }

    @Test
    void searchNullTextTest() {
        assertThat(itemService.search(null))
                .isEmpty();
    }

    @Test
    void updateItemBlankNameTest() {
        Item item = createItem();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName(" ");

        assertThatThrownBy(() ->
                itemService.update(1L, 1L, dto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void updateItemBlankDescriptionTest() {
        Item item = createItem();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setDescription(" ");

        assertThatThrownBy(() ->
                itemService.update(1L, 1L, dto))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void updateByNonOwnerTest() {
        Item item = createItem();

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                itemService.update(999L, 1L, new ItemUpdateDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getItemNotFoundTest() {
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                itemService.getById(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOwnerItemsEmptyTest() {
        User user = createUser();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(1L))
                .thenReturn(List.of());

        assertThat(itemService.getByOwner(1L))
                .isEmpty();
    }

    @Test
    void getItemWithLastAndNextBookingTest() {
        User owner = createUser();

        Item item = createItem();

        Booking lastBooking = createBooking(10L);
        Booking nextBooking = createBooking(20L);

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                any(),
                any(),
                any()))
                .thenReturn(Optional.of(lastBooking));

        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                any(),
                any(),
                any()))
                .thenReturn(Optional.of(nextBooking));

        when(commentRepository.findAllByItemId(1L))
                .thenReturn(List.of());

        ItemDto result =
                itemService.getById(owner.getId(), item.getId());

        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
    }

    @Test
    void getByOwnerLastBookingReplacementTest() {
        User owner = createUser();

        Item item = createItem();

        Booking oldBooking =
                createPastBooking(item, 1L, 10);

        Booking newBooking =
                createPastBooking(item, 2L, 1);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId()))
                .thenReturn(List.of(item));

        when(bookingRepository.findByItemIdInAndStatus(any(), any()))
                .thenReturn(List.of(oldBooking, newBooking));

        Collection<ItemDto> items =
                itemService.getByOwner(owner.getId());

        ItemDto dto = items.iterator().next();

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId())
                .isEqualTo(2L);
    }

    @Test
    void getByOwnerNextBookingReplacementTest() {
        User owner = createUser();

        Item item = createItem();

        Booking farBooking =
                createFutureBooking(item, 1L, 10);

        Booking nearBooking =
                createFutureBooking(item, 2L, 1);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId()))
                .thenReturn(List.of(item));

        when(bookingRepository.findByItemIdInAndStatus(any(), any()))
                .thenReturn(List.of(farBooking, nearBooking));

        Collection<ItemDto> items =
                itemService.getByOwner(owner.getId());

        ItemDto dto = items.iterator().next();

        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId())
                .isEqualTo(2L);
    }

    @Test
    void getByIdForNonOwnerShouldNotLoadBookingsTest() {
        Item item = createItem();

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of());

        ItemDto dto =
                itemService.getById(999L, item.getId());

        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");
        return user;
    }

    private Item createItem() {
        User owner = createUser();

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);

        return item;
    }

    private Booking createBooking(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now());

        User booker = new User();
        booker.setId(id);

        booking.setBooker(booker);
        booking.setItem(createItem());

        return booking;
    }

    private Booking createPastBooking(Item item, Long id, long daysAgo) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        User booker = new User();
        booker.setId(id);

        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(daysAgo));

        return booking;
    }

    private Booking createFutureBooking(Item item, Long id, long daysAhead) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        User booker = new User();
        booker.setId(id);

        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(daysAhead));

        return booking;
    }

    @Test
    void createItemWithNotExistingRequestTest() {
        Long userId = 1L;
        Long requestId = 999L;

        User user = new User();
        user.setId(userId);

        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);
        dto.setRequestId(requestId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(userId, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Request not found");
    }

    @Test
    void createItemUserNotFoundTest() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.create(1L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

}