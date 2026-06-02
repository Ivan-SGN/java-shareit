package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void commentDtoSerializationTest() throws Exception {
        CommentDto dto = getCommentDto();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Good item");

        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("Ivan");
    }

    @Test
    void commentDtoDeserializationTest() throws Exception {
        CommentDto sourceDto = getCommentDto();

        CommentDto parsedDto =
                json.parseObject(json.write(sourceDto).getJson());

        assertThat(parsedDto.getId())
                .isEqualTo(sourceDto.getId());

        assertThat(parsedDto.getText())
                .isEqualTo(sourceDto.getText());

        assertThat(parsedDto.getAuthorName())
                .isEqualTo(sourceDto.getAuthorName());

        assertThat(parsedDto.getCreated())
                .isEqualTo(sourceDto.getCreated());

        assertThat(parsedDto)
                .isEqualTo(sourceDto);
    }

    @Test
    void commentDtoEqualsTest() {
        CommentDto firstDto = getCommentDto();
        CommentDto secondDto = getCommentDto();

        assertThat(firstDto)
                .isEqualTo(secondDto);
    }

    private static CommentDto getCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Good item");
        dto.setAuthorName("Ivan");
        dto.setCreated(LocalDateTime.of(2025, 1, 1, 12, 0));
        return dto;
    }
}