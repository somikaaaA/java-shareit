package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UpdateUserRequest;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.UserIdValid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@UserIdValid @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UpdateUserRequest request, @UserIdValid @PathVariable Long id) {
        request.setId(id);
        return userService.updateUser(request);
    }
}
