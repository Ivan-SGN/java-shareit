package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void itemRequestDtoSerializationTest() throws Exception {
        ItemRequestItemDto item = new ItemRequestItemDto();
        item.setId(10L);
        item.setName("Drill");
        item.setOwnerId(5L);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need drill");
        dto.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));
        dto.setItems(List.of(item));

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need drill");

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-01-01T10:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(10);
    }

    @Test
    void itemRequestDtoDeserializeTest() throws Exception {
        ItemRequestDto sourceDto = new ItemRequestDto();
        sourceDto.setId(1L);
        sourceDto.setDescription("Need drill");
        sourceDto.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));

        ItemRequestDto parsedDto =
                json.parseObject(json.write(sourceDto).getJson());

        assertThat(parsedDto.getId()).isEqualTo(sourceDto.getId());
        assertThat(parsedDto.getDescription())
                .isEqualTo(sourceDto.getDescription());
        assertThat(parsedDto.getCreated())
                .isEqualTo(sourceDto.getCreated());
    }
}