package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void itemEqualsTest() {
        Item firstItem = new Item();
        firstItem.setId(1L);

        Item secondItem = new Item();
        secondItem.setId(1L);

        assertThat(firstItem).isEqualTo(secondItem);
    }

    @Test
    void itemNotEqualsTest() {
        Item firstItem = new Item();
        firstItem.setId(1L);

        Item secondItem = new Item();
        secondItem.setId(2L);

        assertThat(firstItem).isNotEqualTo(secondItem);
        assertThat(firstItem).isNotEqualTo(null);
        assertThat(firstItem).isNotEqualTo("item");
    }
}