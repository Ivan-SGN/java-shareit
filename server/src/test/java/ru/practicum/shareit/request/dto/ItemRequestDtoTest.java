package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void SerializeItemRequestDtoTest() throws Exception {
        ItemRequestDto requestDto = createItemRequestDto();

        assertThat(json.write(requestDto))
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(json.write(requestDto))
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Need drill");

        assertThat(json.write(requestDto))
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-05-30T14:00:00");

        assertThat(json.write(requestDto))
                .extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(10);

        assertThat(json.write(requestDto))
                .extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Drill");
    }

    private ItemRequestDto createItemRequestDto() {
        ItemRequestItemDto itemDto = new ItemRequestItemDto();
        itemDto.setId(10L);
        itemDto.setName("Drill");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need drill");
        requestDto.setCreated(LocalDateTime.of(2026, 5, 30, 14, 0));
        requestDto.setItems(List.of(itemDto));

        return requestDto;
    }
}