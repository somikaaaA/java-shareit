package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidItemIdException;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;

    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(getItemById(id),
                lastBookingForItem(id),
                nextBookingForItem(id),
                commentsForItem(id));
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new InvalidItemIdException("Вещь с id " + id + " не найдена"));
    }

    public List<ItemDto> getItemsForUser(Long userId) {
        List<Object[]> itemsWithDetails = itemRepository.findItemsWithBookingsAndCommentsByUserId(userId);
        return itemsWithDetails.stream()
                .map(itemArray -> {
                    Item item = (Item) itemArray[0];
                    Booking lastBooking = (Booking) itemArray[1];
                    Booking nextBooking = (Booking) itemArray[2];
                    Comment comment = (Comment) itemArray[3];
                    List<Comment> comments = commentService.commentsForItem(item.getId());
                    return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item),
                lastBookingForItem(item.getId()),
                nextBookingForItem(item.getId()),
                commentsForItem(item.getId()));
    }

    @Transactional
    public ItemDto updateItem(UpdateItemRequest request) {
        Item item = getItemById(request.getId());
        ItemMapper.updateItemFields(item, request);
        itemRepository.save(item);
        return ItemMapper.toItemDto(getItemById(item.getId()),
                lastBookingForItem(item.getId()),
                nextBookingForItem(item.getId()),
                commentsForItem(item.getId()));
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return toListItemDto(itemRepository.findByNameContaining(text));
    }

    public boolean isItemRegistered(Long id) {
        return itemRepository.findById(id).isPresent();
    }

    private List<ItemDto> toListItemDto(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            Booking lastBooking = lastBookingForItem(item.getId());
            Booking nextBooking = nextBookingForItem(item.getId());
            List<Comment> comments = commentsForItem(item.getId());
            itemDtos.add(ItemMapper.toItemDto(item, lastBooking, nextBooking, comments));
        }

        return itemDtos;
    }

    private List<Comment> commentsForItem(Long id) {
        return commentService.commentsForItem(id);
    }

    private Booking lastBookingForItem(Long id) { //текущее бронирование
        return bookingService.lastBookingForItem(id);
    }

    private Booking nextBookingForItem(Long id) { //следующее бронирование
        return bookingService.nextBookingForItem(id);
    }
}
