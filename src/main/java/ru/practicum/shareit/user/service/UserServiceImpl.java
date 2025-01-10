package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.InvalidUserIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UpdateUserRequest;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return isEmailRegistered(email);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request) {
        User user = getUserById(request.getId());
        if (request.hasEmail() &&
                !request.getEmail().equals(user.getEmail()) &&
                isEmailRegistered(request.getEmail())) {
            throw new InvalidEmailException("Данный e-mail уже зарегистрирован");
        }
        UserMapper.updateUserFields(user, request);
        userRepository.save(user);
        return UserMapper.toUserDto(getUserById(user.getId()));
    }

    @Override
    public boolean isUserRegistered(Long id) {
        return userRepository.findById(id).isPresent();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new InvalidUserIdException("Пользователь с id " + id + " не найден"));
    }
}
