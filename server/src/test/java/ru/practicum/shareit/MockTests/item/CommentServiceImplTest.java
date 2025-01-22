package ru.practicum.shareit.MockTests.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Item item;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user1@email.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .owner(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .item(item)
                .author(user)
                .build();

        commentDto = CommentDto.builder()
                .text("Comment")
                .build();

        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .end(LocalDateTime.now().minusDays(1)) // Завершено
                .build();
    }

    @Test
    void createComment() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto actualCommentDto = commentService.createComment(user.getId(), commentDto, item.getId());
        assertEquals(commentDto.getText(), actualCommentDto.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createCommentWhenItemNotFound() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
        assertThrows(ItemIdNotFoundException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenUserHasNotBookedItem() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenBookingIsActive() {
        booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .end(LocalDateTime.now().plusDays(1)) // Активное бронирование
                .build();

        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.of(booking));
        assertThrows(BadRequestException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void commentsForItem() {
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        List<Comment> actualComments = commentService.commentsForItem(item.getId());
        assertEquals(1, actualComments.size());
        assertEquals(comment, actualComments.get(0));
        verify(commentRepository).findAllByItemId(item.getId());
    }
}
