package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    private Long id;  //уникальный идентификатор пользователя
    private String name; // имя или логин пользователя
    private String email;  //адрес электронной почты, проверка на уникальность

}
