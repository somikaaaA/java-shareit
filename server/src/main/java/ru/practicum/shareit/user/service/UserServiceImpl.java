package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserIdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UpdateUserRequest;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
               .orElseThrow(() -> new UserIdNotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserRequest request) {
        User user = getUserById(request.getId());
        if (request.hasEmail() &&
                !request.getEmail().equals(user.getEmail()) &&
                isEmailRegistered(request.getEmail())) {
            throw new InvalidEmailException("Данный e-mail уже зарегистрирован");
        }
        UserMapper.updateUserFields(user, request);
        userRepository.save(user);
        return getUser(user.getId());
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public boolean isUserRegistered(Long id) {
        return userRepository.findById(id).isPresent();
    }
}

