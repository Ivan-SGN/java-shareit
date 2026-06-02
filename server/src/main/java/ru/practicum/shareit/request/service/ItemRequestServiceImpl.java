package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User requestor = getUserOrThrow(userId);
        ItemRequest request = itemRequestMapper.toEntity(dto);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);
        log.info("Request created id={} userId={}", savedRequest.getId(), userId);
        return itemRequestMapper.toDto(savedRequest);
    }

    @Override
    public Collection<ItemRequestDto> getOwnRequests(Long userId) {
        log.info("Get own requests userId={}", userId);
        getUserOrThrow(userId);
        List<ItemRequest> requests =
                itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return mapRequests(requests);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Get all requests userId={}", userId);
        getUserOrThrow(userId);
        List<ItemRequest> requests =
                itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return mapRequests(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        log.info("Get request by id={} userId={}", requestId, userId);
        getUserOrThrow(userId);
        ItemRequest request = getRequestOrThrow(requestId);
        ItemRequestDto dto = itemRequestMapper.toDto(request);
        enrichItems(List.of(request), List.of(dto));
        return dto;
    }

    private Collection<ItemRequestDto> mapRequests(List<ItemRequest> requests) {
        List<ItemRequestDto> dtos = requests.stream()
                .map(itemRequestMapper::toDto)
                .toList();
        enrichItems(requests, dtos);
        return dtos;
    }

    private void enrichItems(List<ItemRequest> requests, List<ItemRequestDto> dtos) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = new HashMap<>();

        for (Item item : items) {
            Long requestId = item.getRequest().getId();
            itemsByRequestId
                    .computeIfAbsent(requestId, id -> new java.util.ArrayList<>())
                    .add(item);
        }

        for (int index = 0; index < requests.size(); index++) {
            ItemRequest request = requests.get(index);
            ItemRequestDto dto = dtos.get(index);

            List<Item> requestItems =
                    itemsByRequestId.getOrDefault(request.getId(), List.of());

            dto.setItems(
                    requestItems.stream()
                            .map(itemRequestMapper::toItemDto)
                            .toList()
            );
        }
    }

    private ItemRequest getRequestOrThrow(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Request not found id={}", requestId);
                    return new NotFoundException("Request not found");
                });
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new NotFoundException("User not found");
                });
    }
}