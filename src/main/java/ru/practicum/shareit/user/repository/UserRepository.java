package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    void updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUserById(Long userId);

    boolean isEmailRegistered(String userEmail);


}
