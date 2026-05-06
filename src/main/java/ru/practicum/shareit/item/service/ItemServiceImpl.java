package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long userId, ItemCreateDto itemDto) {
        log.info("Create item by user id={}", userId);

        User owner = getUserOrThrow(userId);
        Item item = itemMapper.toEntity(itemDto, owner);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemDto) {
        log.info("Update item id={} by user id={}", itemId, userId);

        Item item = getItemOrThrow(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("User {} is not owner of item {}", userId, itemId);
            throw new NotFoundException("User is not owner of item");
        }
        validateUpdateFields(itemId, itemDto);
        itemMapper.update(itemDto, item);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        log.info("Get item id={} by user id={}", itemId, userId);
        Item item = getItemOrThrow(itemId);
        ItemDto dto = itemMapper.toDto(item);
        enrichBooking(dto, item, userId);
        enrichComments(dto, item.getId());
        return dto;
    }

    @Override
    public Collection<ItemDto> getByOwner(Long userId) {
        log.info("Get items by owner id={}", userId);
        getUserOrThrow(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> dtos = items.stream().map(itemMapper::toDto).collect(Collectors.toList());
        if (items.isEmpty()) {
            return dtos;
        }
        Map<Long, Booking> lastMap = new HashMap<>();
        Map<Long, Booking> nextMap = new HashMap<>();
        fillBookingMaps(items, lastMap, nextMap);
        enrichDtos(items, dtos, lastMap, nextMap);
        return dtos;
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.info("Search items text={}", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found id={}", itemId);
                    return new NotFoundException("Item not found with id=" + itemId);
                });
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new NotFoundException("User not found with id=" + userId);
                });
    }

    private void validateUpdateFields(Long itemId, ItemUpdateDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            log.warn("Invalid item name for item id={}: blank value", itemId);
            throw new ValidationException("Name must not be blank");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            log.warn("Invalid item description for item id={}: blank value", itemId);
            throw new ValidationException("Description must not be blank");
        }
    }

    private void enrichBooking(ItemDto dto, Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> last = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), now, BookingStatus.APPROVED);
        Optional<Booking> next = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), now, BookingStatus.APPROVED);
        dto.setLastBooking(last.map(this::toShortDto).orElse(null));
        dto.setNextBooking(next.map(this::toShortDto).orElse(null));
    }

    private BookingShortDto toShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

    private void fillBookingMaps(List<Item> items, Map<Long, Booking> lastMap, Map<Long, Booking> nextMap) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);
        for (Booking booking : bookings) {
            Long itemId = booking.getItem().getId();
            if (booking.getStart().isBefore(now)) {
                Booking current = lastMap.get(itemId);
                if (current == null || booking.getStart().isAfter(current.getStart())) {
                    lastMap.put(itemId, booking);
                }
            } else {
                Booking current = nextMap.get(itemId);
                if (current == null || booking.getStart().isBefore(current.getStart())) {
                    nextMap.put(itemId, booking);
                }
            }
        }
    }

    private void enrichDtos(List<Item> items, List<ItemDto> dtos, Map<Long, Booking> lastMap, Map<Long, Booking> nextMap) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            ItemDto dto = dtos.get(i);
            Booking last = lastMap.get(item.getId());
            Booking next = nextMap.get(item.getId());
            dto.setLastBooking(last != null ? toShortDto(last) : null);
            dto.setNextBooking(next != null ? toShortDto(next) : null);
        }
    }

    private void enrichComments(ItemDto dto, Long itemId) {
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        dto.setComments(comments);
    }
}