package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.stateStrategy.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")

@Builder
@Entity
@Table(name = "bookings", schema = "public")
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //уникальный идентификатор бронирования
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; // дата и время начала бронирования
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; //  дата и время конца бронирования
    @ManyToOne
    @JoinColumn(name = "item", referencedColumnName = "id", nullable = false)
    private Item item;    //вещь, которую пользователь бронирует;
    @ManyToOne
    @JoinColumn(name = "booker", referencedColumnName = "id", nullable = false)
    private User booker; //пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // статус бронирования

    public Booking() {
    }

}
