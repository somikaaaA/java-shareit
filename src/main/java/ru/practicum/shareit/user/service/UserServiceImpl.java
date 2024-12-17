package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UpdateUserRequest;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id).get());
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userRepository.isEmailRegistered(email);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUserById(id);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request) {
        User user = userRepository.getUserById(request.getId()).get();
        if (request.hasEmail() &&
                !request.getEmail().equals(user.getEmail()) &&
                isEmailRegistered(request.getEmail())) {
            throw new BadRequestException("Данный e-mail уже зарегистрирован");
        }
        UserMapper.updateUserFields(user, request);
        userRepository.updateUser(user);
        return UserMapper.toUserDto(userRepository.getUserById(user.getId()).get());
    }

    @Override
    public boolean isUserRegistered(Long id) {
        return userRepository.getUserById(id).isPresent();
    }
}
