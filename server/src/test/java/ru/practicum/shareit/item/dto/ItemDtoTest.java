package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void itemDtoSerializationTest() throws Exception {
        ItemDto dto = getItemDto();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Drill");

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Power drill");

        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isTrue();

        assertThat(result).extractingJsonPathNumberValue("$.ownerId")
                .isEqualTo(5);

        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(6);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(2);

        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("Good item");
    }

    @Test
    void itemDtoDeserializationTest() throws Exception {
        ItemDto sourceDto = getItemDto();

        ItemDto parsedDto =
                json.parseObject(json.write(sourceDto).getJson());

        assertThat(parsedDto.getId()).isEqualTo(sourceDto.getId());

        assertThat(parsedDto.getName())
                .isEqualTo(sourceDto.getName());

        assertThat(parsedDto.getDescription())
                .isEqualTo(sourceDto.getDescription());

        assertThat(parsedDto.getAvailable())
                .isEqualTo(sourceDto.getAvailable());

        assertThat(parsedDto.getOwnerId())
                .isEqualTo(sourceDto.getOwnerId());

        assertThat(parsedDto.getRequestId())
                .isEqualTo(sourceDto.getRequestId());
    }

    @Test
    void itemDtoEqualsTest() {
        ItemDto firstDto = getItemDto();
        ItemDto secondDto = getItemDto();

        assertThat(firstDto)
                .isEqualTo(secondDto);
    }

    @Test
    void itemDtoNotEqualsTest() {
        ItemDto firstDto = getItemDto();
        ItemDto secondDto = getItemDto();

        secondDto.setId(999L);

        assertThat(firstDto)
                .isNotEqualTo(secondDto);
    }

    private static ItemDto getItemDto() {
        BookingShortDto lastBooking = new BookingShortDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(10L);

        BookingShortDto nextBooking = new BookingShortDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(20L);

        CommentDto comment = new CommentDto();
        comment.setId(100L);
        comment.setText("Good item");

        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Drill");
        dto.setDescription("Power drill");
        dto.setAvailable(true);
        dto.setOwnerId(5L);
        dto.setRequestId(6L);
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(List.of(comment));
        return dto;
    }
}