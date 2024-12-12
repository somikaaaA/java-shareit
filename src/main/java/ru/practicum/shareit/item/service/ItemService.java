package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(UpdateItemRequest request);

    ItemDto getItem(Long id);

    List<ItemDto> getItemsForUser(Long userId);

    List<ItemDto> searchItems(String text);

    boolean isItemRegistered(Long id);
}
