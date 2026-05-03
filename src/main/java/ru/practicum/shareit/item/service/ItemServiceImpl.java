package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

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
    public ItemDto getById(Long itemId) {
        log.info("Get item id={}", itemId);
        return itemMapper.toDto(getItemOrThrow(itemId));
    }

    @Override
    public Collection<ItemDto> getByOwner(Long userId) {
        log.info("Get items by owner id={}", userId);

        getUserOrThrow(userId);

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
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
}