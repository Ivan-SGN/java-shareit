package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto dto);

    UserDto update(Long userId, UserDto dto);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    void delete(Long userId);
}