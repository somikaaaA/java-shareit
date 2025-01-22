package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class CommentDto {
    private Long id;
    private String text;
}
