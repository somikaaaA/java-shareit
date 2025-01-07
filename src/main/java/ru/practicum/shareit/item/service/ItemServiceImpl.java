package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item),
                lastBookingForItem(item.getId()),
                nextBookingForItem(item.getId()),
                commentsForItem(item.getId()));
    }

    @Override
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

    @Override
    public ItemDto getItem(Long id) {
        return null;
    }

    @Override
    public List<ItemDto> getItemsForUser(Long userId) {
        return List.of();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return toListItemDto(itemRepository.findByNameContaining(text));
    }

    @Override
    public boolean isItemRegistered(Long id) {
        return itemRepository.findById(id).isPresent();
    }

    @Override
    public Item getItemById(Long id) {
        return null;
    }

    private List<ItemDto> toListItemDto(List<Item> list) {
        return list.stream()
                .map(item ->
                        ItemMapper.toItemDto(item,
                                lastBookingForItem(item.getId()),
                                nextBookingForItem(item.getId()),
                                commentsForItem(item.getId()))
                )
                .toList();
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