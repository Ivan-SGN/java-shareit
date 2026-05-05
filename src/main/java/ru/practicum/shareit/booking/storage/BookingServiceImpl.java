package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User booker = getUserOrThrow(userId);
        Item item = getItemOrThrow(dto.getItemId());
        validateCreate(dto, userId, item);
        Booking booking = bookingMapper.toEntity(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created id={} by userId={} for itemId={}", savedBooking.getId(), userId, item.getId());
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("User {} is not owner of item {}", ownerId, booking.getItem().getId());
            throw new NotFoundException("User is not owner");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            log.warn("Booking {} already processed with status {}", bookingId, booking.getStatus());
            throw new ValidationException("Booking already processed");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated to {}", bookingId, updatedBooking.getStatus());
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied");
        }
        log.info("Get booking id={} by userId={}", bookingId, userId);
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByUser(Long userId) {
        getUserOrThrow(userId);
        List<BookingDto> result = bookingRepository.findByBookerIdOrderByStartDesc(userId)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
        log.info("Get bookings for userId={}", userId);
        return result;
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId) {
        getUserOrThrow(ownerId);
        List<BookingDto> result = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
        log.info("Get bookings for ownerId={}", ownerId);
        return result;
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found id={}", bookingId);
                    return new NotFoundException("Booking not found");
                });
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new NotFoundException("User not found");
                });
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found id={}", itemId);
                    return new NotFoundException("Item not found");
                });
    }

    private void validateCreate(BookingCreateDto dto, Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Owner {} tried to book own item {}", userId, item.getId());
            throw new ValidationException("Owner cannot book own item");
        }
        if (!item.getAvailable()) {
            log.warn("Item {} is not available", item.getId());
            throw new ValidationException("Item is not available");
        }
        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().isEqual(dto.getEnd())) {
            log.warn("Invalid booking time start={} end={}", dto.getStart(), dto.getEnd());
            throw new ValidationException("Invalid booking time");
        }
    }
}
