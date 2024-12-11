package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    private String userName;
    @Email(message = "Проверьте корректность email")
    @NotBlank(message = "Поле не должно быть пустым. Укажите email")
    private String userEmail;
}
