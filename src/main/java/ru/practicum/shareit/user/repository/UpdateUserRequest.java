package ru.practicum.shareit.user.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private Long id;
    private String name;
    private String email;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
}
