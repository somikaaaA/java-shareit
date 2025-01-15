package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);

    List<Comment> commentsForItem(Long id);
}
