package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentMapperTest {

    private final CommentMapper commentMapper;

    @Test
    void commentCreateDtoToEntityTest() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Good item");

        Comment comment =
                commentMapper.toEntity(dto);

        assertThat(comment.getText())
                .isEqualTo(dto.getText());

        assertThat(comment.getId()).isNull();
        assertThat(comment.getItem()).isNull();
        assertThat(comment.getAuthor()).isNull();
        assertThat(comment.getCreated()).isNull();
    }

    @Test
    void commentToDtoTest() {
        User author = new User();
        author.setName("Ivan");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Good item");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2026, 1, 1, 10, 0));

        CommentDto dto =
                commentMapper.toDto(comment);

        assertThat(dto.getId())
                .isEqualTo(comment.getId());

        assertThat(dto.getText())
                .isEqualTo(comment.getText());

        assertThat(dto.getAuthorName())
                .isEqualTo(author.getName());

        assertThat(dto.getCreated())
                .isEqualTo(comment.getCreated());
    }
}