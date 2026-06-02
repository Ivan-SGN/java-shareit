package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestor", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toEntity(ItemRequestCreateDto dto);

    @Mapping(target = "items", ignore = true)
    ItemRequestDto toDto(ItemRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemRequestItemDto toItemDto(Item item);

}