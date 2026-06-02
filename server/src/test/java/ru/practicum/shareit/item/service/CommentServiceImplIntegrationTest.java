package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplIntegrationTest {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Test
    void createCommentTest() {
        User owner = userRepository.save(createOwner());
        User booker = userRepository.save(createBooker());

        Item item = itemRepository.save(createItem(owner));

        bookingRepository.save(createBooking(item, booker));

        CommentDto createdComment = commentService.create(
                createCommentDto(),
                booker.getId(),
                item.getId()
        );

        Comment savedComment = commentRepository.findById(createdComment.getId())
                .orElseThrow();

        assertThat(savedComment.getText()).isEqualTo("Great item");
        assertThat(savedComment.getAuthor().getId()).isEqualTo(booker.getId());
        assertThat(savedComment.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createCommentWithoutBookingTest() {
        User owner = userRepository.save(createOwner());
        User booker = userRepository.save(createBooker());

        Item item = itemRepository.save(createItem(owner));

        assertThatThrownBy(() ->
                commentService.create(
                        createCommentDto(),
                        booker.getId(),
                        item.getId()
                )
        ).isInstanceOf(ValidationException.class)
                .hasMessage("User has not completed booking for this item");
    }

    @Test
    void createCommentForUnknownItemTest() {
        User booker = userRepository.save(createBooker());

        assertThatThrownBy(() ->
                commentService.create(
                        createCommentDto(),
                        booker.getId(),
                        999L
                )
        ).isInstanceOf(NotFoundException.class);
    }

    private User createOwner() {
        User user = new User();
        user.setName("Owner");
        user.setEmail("owner@test.ru");
        return user;
    }

    private User createBooker() {
        User user = new User();
        user.setName("Booker");
        user.setEmail("booker@test.ru");
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

    private Booking createBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        return booking;
    }

    private CommentCreateDto createCommentDto() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item");
        return dto;
    }
}