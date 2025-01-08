package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UpdateUserRequest;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(Long id);

    User getUserById(Long id);

    boolean isEmailRegistered(String email);

    void deleteUser(Long id);

    UserDto updateUser(UpdateUserRequest request);

    boolean isUserRegistered(Long id);
}
