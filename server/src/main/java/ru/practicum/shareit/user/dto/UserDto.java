package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.validation.EmailRepeatValid;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    @Email(message = "Некорректное указание E-mail")
    @NotBlank(message = "email должен быть указан")
    @EmailRepeatValid
    private String email;
}
