package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId, BookingStatus status);

    List<Booking> findByItemIdInAndStatus(
            List<Long> itemIds, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
            Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime now, BookingStatus status);

}