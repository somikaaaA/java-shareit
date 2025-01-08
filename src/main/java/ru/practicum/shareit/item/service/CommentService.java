package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);

    List<Comment> commentsForItem(Long id);
}
