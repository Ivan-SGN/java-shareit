package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto create(CommentCreateDto commentDto, Long userId, Long itemId) {
        Item item = getItemOrThrow(itemId);
        validateCreateComment(userId, itemId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(getAuthorOrThrow(userId));
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created itemId={} userId={} commentId={}", itemId, userId, savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    private void validateCreateComment(Long userId, Long itemId) {
        boolean hasBooking = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(
                        userId,
                        itemId,
                        BookingStatus.APPROVED,
                        java.time.LocalDateTime.now()
                );
        if (!hasBooking) {
            log.warn("User {} cannot comment item {} without completed booking", userId, itemId);
            throw new ValidationException("User has not completed booking for this item");
        }
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found id={}", itemId);
                    return new NotFoundException("Item not found");
                });
    }

    private User getAuthorOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new NotFoundException("User not found");
                });
    }
}
