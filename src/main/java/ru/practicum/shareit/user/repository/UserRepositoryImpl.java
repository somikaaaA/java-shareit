package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl  implements UserRepository{
    private final Map<Long, User> userStorage;
    private Long userId = 0L;

    @Override
    public User createUser(User user) {
        user.setUserId(userId++);
        userStorage.put(user.getUserId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    @Override
    public void updateUser(User user) {
        userStorage.put(user.getUserId(), user);
        log.info("Пользователь с id {} обновлен", user.getUserId());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        log.info("Поиск пользователя с id {}", userId);
        return Optional.ofNullable(userStorage.get(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.remove(userId);
        log.info("Пользователь с id {} удален", userId);
    }

    @Override
    public boolean isEmailRegistered(String userEmail) {
        return userStorage.values().stream()
                .map(User::getUserEmail)
                .anyMatch(k -> k.equals(userEmail));
    }
}