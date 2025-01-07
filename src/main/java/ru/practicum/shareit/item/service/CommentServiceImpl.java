package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InvalidItemIdException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        log.info("Поиск пользователя с id {} который хочет добавить комментарий", userId);
        User author = userService.getUserById(userId);
        log.info("Поиск вещи с id {} для которой добавляется комментарий", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidItemIdException("Вещь с id " + itemId + " не найдена"));
        Booking booking = bookingRepository.findByBookerIdAndItemId(userId, itemId)
                .orElseThrow(() ->
                        new BadRequestException("Пользователь " + author.getName() + " id " + author.getId() + " не брал вещь " + item.getName() + " id " + item.getId() + " в аренду"));

        if (booking.getEnd().isAfter(LocalDateTime.now())) { //нельзя комментировать текущее бронирование
            throw new BadRequestException("Пользователь " + author.getName() + " комментирует вещь " + item.getName() +
                    " при текущем бронировании");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, author);
        log.info("Сохранение комментария {} в базу данных", comment);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<Comment> commentsForItem(Long id) {
        log.info("Поиск комментариев для вещи с id {}", id);
        return commentRepository.findAllByItemId(id);
    }
}
