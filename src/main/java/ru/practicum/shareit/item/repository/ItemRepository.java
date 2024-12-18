package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    void updateItem(Item item);

    Optional<Item> getItemById(Long id);

    List<Item> getItemsForUser(Long userId);

    List<Item> searchItems(String text);
}
