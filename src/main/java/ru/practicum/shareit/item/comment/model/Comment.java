package ru.practicum.shareit.item.comment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")

@Builder
@Entity
@Table(name = "comments", schema = "public")
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "item", referencedColumnName = "id", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id", nullable = false)
    private User author;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public Comment() {
    }
}
