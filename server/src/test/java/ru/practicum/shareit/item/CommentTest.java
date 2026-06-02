package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void commentSerializationTest() {
        Comment firstComment = new Comment();
        firstComment.setId(1L);
        firstComment.setText("Comment text");

        Comment secondComment = new Comment();
        secondComment.setId(1L);
        secondComment.setText("Another text");

        assertThat(firstComment)
                .isEqualTo(secondComment);
    }

    @Test
    void commentDeserializationTest() {
        Comment firstComment = new Comment();
        firstComment.setId(1L);

        Comment secondComment = new Comment();
        secondComment.setId(2L);

        assertThat(firstComment)
                .isNotEqualTo(secondComment);
    }
}