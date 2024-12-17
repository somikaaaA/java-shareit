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

    public Item createItem(Item item) {
        item.setId(++id);
        //log.info("Добавление вещи {} ", item);
        itemStorage.put(item.getId(), item);
        //log.info("Вещь {} добавлена в базу данных", item);
        log.info("Вещь добавлена в базу данных. Присвоен id = {}", item.getId());
        return item;
    }

    public void updateItem(Item item) {
        //log.info("Обновление вещи с id {}", item.getId());
        itemStorage.put(item.getId(), item);
        log.info("Обновленная вещь с id = {} добавлена в базу данных", item);
    }

    public Optional<Item> getItemById(Long id) {
        log.info("Поиск вещи с id = {}", id);
        return Optional.ofNullable(itemStorage.get(id));
    }

    public List<Item> getItemsForUser(Long userId) {
        log.info("Поиск вещей для пользователя с id = {}", userId);
        return itemStorage.values().stream()
                .filter(k -> k.getOwner().equals(userId))
                .toList();
    }

    public List<Item> searchItems(String text) {
        log.info("Поиск вещей по совпадению с \"{}\"", text);
        return itemStorage.values().stream()
                .filter(k -> (k.getName().matches("(?i).*" + Pattern.quote(text) + ".*") ||
                        k.getDescription().matches("(?i).*" + Pattern.quote(text) + ".*")) &&
                        k.getAvailable())
                .toList();
    }
}
