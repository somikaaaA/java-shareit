package ru.practicum.shareit.user.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private Long userId;
    private String userName;
    private String userEmail;

    public boolean hasName() {
        return userName.isBlank();
    }

    public boolean hasEmail() {
        return userEmail.isBlank();
    }
}
