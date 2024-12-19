package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    User createUser(User user);

    void updateUser(User user);

    void deleteUserById(Long id);

    boolean isEmailRegistered(String email);
}
