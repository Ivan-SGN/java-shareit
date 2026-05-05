package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
}