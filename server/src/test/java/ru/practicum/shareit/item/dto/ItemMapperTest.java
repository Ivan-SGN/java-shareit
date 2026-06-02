package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemMapperTest {

    private final ItemMapper itemMapper;

    @Test
    void itemToDtoTest() {
        User owner = new User();
        owner.setId(10L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = itemMapper.toDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwnerId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Power drill");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void itemToEntityTest() {
        User owner = new User();
        owner.setId(10L);

        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);

        Item item = itemMapper.toEntity(dto, owner);

        assertThat(item.getName()).isEqualTo(dto.getName());
        assertThat(item.getDescription()).isEqualTo(dto.getDescription());
        assertThat(item.getAvailable()).isEqualTo(dto.getAvailable());
        assertThat(item.getOwner()).isEqualTo(owner);
    }

    @Test
    void itemUpdateTest() {
        Item item = new Item();
        item.setName("Old");
        item.setDescription("Old description");
        item.setAvailable(true);

        ItemUpdateDto dto = new ItemUpdateDto();

        itemMapper.update(dto, item);

        assertThat(item.getName()).isEqualTo("Old");

        assertThat(item.getDescription())
                .isEqualTo("Old description");

        assertThat(item.getAvailable())
                .isTrue();
    }
}