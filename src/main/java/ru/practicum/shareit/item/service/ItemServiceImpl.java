package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.UpdateItemRequest;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(UpdateItemRequest request) {
        Item item = itemRepository.getItemById(request.getId()).get();
        ItemMapper.updateFields(item, request);
        itemRepository.updateItem(item);
        return ItemMapper.toItemDto(itemRepository.getItemById(item.getId()).get());
    }

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id).get());
    }

    @Override
    public List<ItemDto> getItemsForUser(Long userId) {
        return toListItemDto(itemRepository.getItemsForUser(userId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return toListItemDto(itemRepository.searchItems(text));
    }

    @Override
    public boolean isItemRegistered(Long id) {
        return itemRepository.getItemById(id).isPresent();
    }

    private List<ItemDto> toListItemDto(List<Item> list) {
        return list.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
