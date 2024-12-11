package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UpdateUserRequest;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .build();
    }

    public static User toUser (UserDto userDto) {
        return User.builder()
                .userId(userDto.getUserId())
                .userName(userDto.getUserName())
                .userEmail(userDto.getUserEmail())
                .build();
    }

    public static void updateFields(User user, UpdateUserRequest request) {
        if (request.hasName()) {
            user.setUserName(request.getUserName());
        }
        if (request.hasEmail()) {
            user.setUserEmail(request.getUserEmail());
        }
    }
}
