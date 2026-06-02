package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") Long userId,
                          @RequestBody UserDto dto) {
        return userService.update(userId, dto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable("userId") Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }
}