package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ItemIdValid;
import ru.practicum.shareit.validation.UserIdValid;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ItemDto getItem(@ItemIdValid @PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getItemsForUser(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsForUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    public ItemDto addItem(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody UpdateItemRequest request, @ItemIdValid @PathVariable Long id) {
        request.setId(id);
        request.setOwner(userId);
        return itemService.updateItem(request);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @ItemIdValid @PathVariable Long id) {

        return commentService.createComment(userId, commentDto, id);
    }
}
