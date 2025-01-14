package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")

@Builder
@Entity
@Table(name = "users", schema = "public")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //уникальный идентификатор пользователя
    @Column(nullable = false)
    private String name; // имя или логин пользователя
    @Column(nullable = false, unique = true)
    private String email;  //адрес электронной почты, проверка на уникальность

    public User() {
    }
}
