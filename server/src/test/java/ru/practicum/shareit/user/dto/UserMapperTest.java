package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserMapperTest {

    private final UserMapper userMapper;

    @Test
    void userToDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");

        UserDto dto = userMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(user.getId());

        assertThat(dto.getName())
                .isEqualTo(user.getName());

        assertThat(dto.getEmail())
                .isEqualTo(user.getEmail());
    }

    @Test
    void userToEntityTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Ivan");
        dto.setEmail("ivan@test.ru");

        User user = userMapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(dto.getId());

        assertThat(user.getName())
                .isEqualTo(dto.getName());

        assertThat(user.getEmail())
                .isEqualTo(dto.getEmail());
    }

    @Test
    void userUpdateTest() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");

        UserDto dto = new UserDto();

        userMapper.update(dto, user);

        assertThat(user.getName())
                .isEqualTo("Ivan");

        assertThat(user.getEmail())
                .isEqualTo("ivan@test.ru");
    }
}