package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper;

    @Test
    void itemRequestToEntityTest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need drill");

        ItemRequest request =
                itemRequestMapper.toEntity(dto);

        assertThat(request.getDescription())
                .isEqualTo(dto.getDescription());

        assertThat(request.getRequestor()).isNull();
        assertThat(request.getCreated()).isNull();
    }

    @Test
    void itemRequestToDtoTest() {
        User requestor = new User();
        requestor.setId(10L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need drill");
        request.setCreated(LocalDateTime.now());
        request.setRequestor(requestor);

        ItemRequestDto dto =
                itemRequestMapper.toDto(request);

        assertThat(dto.getId())
                .isEqualTo(request.getId());

        assertThat(dto.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(dto.getCreated())
                .isEqualTo(request.getCreated());

        assertThat(dto.getItems()).isNull();
    }

    @Test
    void itemToItemRequestItemDtoTest() {
        User owner = new User();
        owner.setId(15L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setOwner(owner);

        ItemRequestItemDto dto =
                itemRequestMapper.toItemDto(item);

        assertThat(dto.getId())
                .isEqualTo(item.getId());

        assertThat(dto.getName())
                .isEqualTo(item.getName());

        assertThat(dto.getOwnerId())
                .isEqualTo(owner.getId());
    }
}