package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void CreateUserTest() {
        UserDto userDto = createUserDto();

        UserDto createdUser = userService.create(userDto);

        User savedUser = userRepository.findById(createdUser.getId())
                .orElseThrow();

        assertThat(savedUser.getName()).isEqualTo("Ivan");
        assertThat(savedUser.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void CreateUserWithDuplicateEmailTest() {
        userService.create(createUserDto());

        UserDto duplicateUser = createUserDto();
        duplicateUser.setName("Petr");

        assertThatThrownBy(() -> userService.create(duplicateUser))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void GetUserByIdTest() {
        User user = new User();
        user.setName("Ivan");
        user.setEmail("ivan@test.ru");

        User savedUser = userRepository.save(user);

        UserDto foundUser = userService.getById(savedUser.getId());

        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.getName()).isEqualTo("Ivan");
        assertThat(foundUser.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void GetAllUsersTest() {
        User firstUser = new User();
        firstUser.setName("Ivan");
        firstUser.setEmail("ivan@test.ru");

        User secondUser = new User();
        secondUser.setName("Petr");
        secondUser.setEmail("petr@test.ru");

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        Collection<UserDto> users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void UpdateUserTest() {
        UserDto createdUser = userService.create(createUserDto());

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated");
        updateDto.setEmail("updated@test.ru");

        userService.update(createdUser.getId(), updateDto);

        User updatedUser = userRepository.findById(createdUser.getId())
                .orElseThrow();

        assertThat(updatedUser.getName()).isEqualTo("Updated");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.ru");
    }

    @Test
    void DeleteUserTest() {
        UserDto createdUser = userService.create(createUserDto());

        userService.delete(createdUser.getId());

        assertThat(userRepository.existsById(createdUser.getId()))
                .isFalse();
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("Ivan");
        userDto.setEmail("ivan@test.ru");
        return userDto;
    }
}