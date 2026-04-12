package ru.practicum.shareit.item.dto;

import org.mapstruct.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", ignore = true)
    ItemDto toDto(Item item);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toEntity(ItemDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ItemDto itemDto, @org.mapstruct.MappingTarget Item item);

    default Item toEntity(ItemDto dto, User owner, ItemRequest request) {
        Item item = toEntity(dto);
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }
}