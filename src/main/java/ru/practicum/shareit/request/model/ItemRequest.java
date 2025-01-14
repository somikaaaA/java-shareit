package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //уникальный идентификатор запроса
    @Column(nullable = false)
    private String description; // текст запроса, содержащий описание требуемой вещи
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private User requestor; //пользователь, создавший запрос
    private LocalDateTime created; // дата и время создания запроса
}
