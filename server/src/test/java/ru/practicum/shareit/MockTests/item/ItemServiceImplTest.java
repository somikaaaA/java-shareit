package ru.practicum.shareit.MockTests.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;

    private ItemDto itemDto, expectedItemDto;
    private Item item, updatedItem;
    private UpdateItemRequest request;
    private Comment commentOne, commentTwo;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .build();
        commentOne = Comment.builder()
                .id(1L)
                .text("CommentOne")
                .build();
        commentTwo = Comment.builder()
                .id(2L)
                .text("CommentTwo")
                .build();
        expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .comments(List.of(commentOne, commentTwo))
                .available(true)
                .build();
        updatedItem = Item.builder()
                .id(1L)
                .name("item1Updated name")
                .description("item1Updated description")
                .available(true)
                .build();
        request = UpdateItemRequest.builder()
                .id(1L)
                .name("item1Updated name")
                .description("item1Updated description")
                .build();

    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(List.of(commentOne, commentTwo));
        when(bookingService.lastBookingForItem(itemDto.getId()))
                .thenReturn(null);
        when(bookingService.nextBookingForItem(itemDto.getId()))
                .thenReturn(null);
        ItemDto actualItemDto = itemService.createItem(1L, itemDto);
        assertEquals(expectedItemDto, actualItemDto);

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);//тестируем метод, чтобы убедиться,
        //что в базу передается правильный объект
        verify(itemRepository).save(itemCaptor.capture()); //захваченный объект, который был передан в метод
        Item capturedItem = itemCaptor.getValue();
        assertEquals(user, capturedItem.getOwner()); //проверяем, что хозяин вещи сохранен в базу

        verify(userService).getUserById(user.getId());
        verify(itemRepository).save(any(Item.class));
        verify(bookingService).lastBookingForItem(item.getId());
        verify(bookingService).nextBookingForItem(item.getId());
    }

    @Test
    void updatedItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(List.of(commentOne, commentTwo));
        when(bookingService.lastBookingForItem(itemDto.getId()))
                .thenReturn(null);
        when(bookingService.nextBookingForItem(itemDto.getId()))
                .thenReturn(null);
        ItemDto actualItemDto = itemService.updateItem(request);

        assertEquals(updatedItem.getName(), actualItemDto.getName());
        assertEquals(updatedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(2, actualItemDto.getComments().size());
    }

    @Test
    void updateItemWhenItemNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(ItemIdNotFoundException.class, () -> itemService.updateItem(request));
        verify(itemRepository).findById(request.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingService.lastBookingForItem(item.getId())).thenReturn(null);
        when(bookingService.nextBookingForItem(item.getId())).thenReturn(null);
        when(commentService.commentsForItem(item.getId())).thenReturn(List.of(commentOne, commentTwo));

        ItemDto actualItemDto = itemService.getItem(item.getId());

        assertEquals(expectedItemDto, actualItemDto);
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getItemByIdWhenItemExists() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Item actualItem = itemService.getItemById(item.getId());
        assertEquals(item, actualItem);
    }

    @Test
    void getItemsForUser() {
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(List.of(item));
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(List.of(commentOne, commentTwo));
        List<ItemDto> actualItems = itemService.getItemsForUser(user.getId());
        assertEquals(1, actualItems.size());
        assertEquals(expectedItemDto, actualItems.get(0));
    }

    @Test
    void searchItemsWhenTextIsEmpty() {
        List<ItemDto> actualItems = itemService.searchItems("");
        assertEquals(0, actualItems.size());
    }

    @Test
    void searchItemsWhenTextIsNotEmpty() {
        when(itemRepository.findByNameContaining("item")).thenReturn(List.of(item));
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(List.of(commentOne, commentTwo));
        List<ItemDto> actualItems = itemService.searchItems("item");
        assertEquals(1, actualItems.size());
        assertEquals(expectedItemDto, actualItems.get(0));
    }

    @Test
    void searchItemByRequest() {
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(List.of(commentOne, commentTwo));
        List<ItemDto> actualItems = itemService.searchItemByRequest(1L);
        assertEquals(1, actualItems.size());
        assertEquals(expectedItemDto, actualItems.get(0));
    }

    @Test
    void isItemRegisteredWhenItemDoesNotExist() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        boolean isRegistered = itemService.isItemRegistered(99L);
        assertEquals(false, isRegistered);
    }
}
