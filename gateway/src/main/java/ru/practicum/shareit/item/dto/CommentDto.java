package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
}
