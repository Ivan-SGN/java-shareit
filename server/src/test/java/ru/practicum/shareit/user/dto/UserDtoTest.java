package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void userDtoSerializationTest() {
        UserDto firstUser = new UserDto();
        firstUser.setId(1L);
        firstUser.setName("Ivan");
        firstUser.setEmail("ivan@test.ru");

        UserDto secondUser = new UserDto();
        secondUser.setId(1L);
        secondUser.setName("Ivan");
        secondUser.setEmail("ivan@test.ru");

        assertThat(firstUser)
                .isEqualTo(secondUser);
    }

    @Test
    void userDtoDeserializationTest() {
        UserDto firstUser = new UserDto();
        firstUser.setId(1L);
        firstUser.setName("Ivan");
        firstUser.setEmail("ivan@test.ru");

        UserDto secondUser = new UserDto();
        secondUser.setId(2L);
        secondUser.setName("Petr");
        secondUser.setEmail("petr@test.ru");

        assertThat(firstUser)
                .isNotEqualTo(secondUser);
    }
}