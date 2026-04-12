package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Create user id={}", userDto.getId());
        validateEmailUnique(userDto.getEmail(), null);
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Update user id={}", userId);

        User user = getUserOrThrow(userId);
        if (userDto.getEmail() != null) {
            validateEmailUnique(userDto.getEmail(), userId);
        }

        userMapper.update(userDto, user);

        return userMapper.toDto(userStorage.update(user));
    }

    @Override
    public UserDto getById(Long userId) {
        log.info("Get user id={}", userId);
        return userMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("Get all users");
        return userStorage.getAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        log.info("Delete user id={}", userId);
        getUserOrThrow(userId);
        userStorage.delete(userId);
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.getById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found id={}", userId);
                    return new NotFoundException("User not found with id=" + userId);
                });
    }

    private void validateEmailUnique(String email, Long excludeUserId) {
        userStorage.getAll().forEach(user -> {
            if (user.getEmail().equals(email) && (!user.getId().equals(excludeUserId))) {
                log.warn("Email already exists: {}", email);
                throw new ConflictException("Email already exists");
            }
        });
    }
}
