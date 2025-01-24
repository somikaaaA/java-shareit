package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(getItemById(id),
                lastBookingForItem(id),
                nextBookingForItem(id),
                commentsForItem(id));
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemIdNotFoundException("Вещь с id " + id + " не найдена"));
    }

    @Override
    public List<ItemDto> getItemsForUser(Long userId) {
        return toListItemDto(itemRepository.findByOwnerId(userId));
    }


    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userService.getUserById(userId);
        ItemRequest request = Optional.ofNullable(itemDto.getRequestId()) //проверка если передан id запроса
                .flatMap(requestRepository::findById)
                .orElse(null);
        Item item = ItemMapper.toItem(itemDto, user, request);
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
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return toListItemDto(itemRepository.findByNameContaining(text));
    }

    @Override
    public List<ItemDto> searchItemByRequest(Long id) {
        return toListItemDto(itemRepository.findByRequestId(id));
    }

    @Override
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
