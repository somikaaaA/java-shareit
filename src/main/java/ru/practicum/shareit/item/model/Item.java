package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")

@Builder
@Entity
@Table(name = "items", schema = "public")
@AllArgsConstructor
public class Item { //вещь
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //уникальный идентификатор вещи
    @Column(nullable = false)
    private String name; //краткое название, не должно быть пустым
    @Column(nullable = false)
    private String description; // развёрнутое описание
    @Column(nullable = false)
    private Boolean available; //статус о том, доступна или нет вещь для аренды //должен быть указан
    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = false)
    private User owner; //владелец вещи
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request", referencedColumnName = "id")
    private ItemRequest request; // если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос

    public Item() {
    }
}
