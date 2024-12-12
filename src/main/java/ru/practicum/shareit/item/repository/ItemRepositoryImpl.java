package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository{
    private final Map<Long, Item> itemStorage;
    private Long id = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(++id);
        itemStorage.put(item.getId(),item);
        log.info("Вещь {} добавлена", item);
        return item;
    }

    @Override
    public void updateItem(Item item) {
        itemStorage.put(item.getId(), item);
        log.info("Вещь с id {} обновлена", item.getId());
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        log.info("Поиск вещи с id {}", id);
        return Optional.ofNullable(itemStorage.get(id));
    }

    @Override
    public List<Item> getItemsForUser(Long userId) {
        log.info("Поиск вещей для пользователя с id {}", userId);
        return itemStorage.values().stream()
                .filter(k -> k.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Поиск вещей по совпадению с \"{}\"", text);
        return itemStorage.values().stream()
                .filter(k -> (k.getName().matches("(?i).*" + Pattern.quote(text) + ".*") ||
                        k.getDescription().matches("(?i).*" + Pattern.quote(text) + ".*")) &&
                        k.getAvailable())
                .toList();
    }
}
