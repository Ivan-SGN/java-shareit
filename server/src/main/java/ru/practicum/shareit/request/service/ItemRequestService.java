package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto dto);

    Collection<ItemRequestDto> getOwnRequests(Long userId);

    Collection<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getById(Long userId, Long requestId);

}