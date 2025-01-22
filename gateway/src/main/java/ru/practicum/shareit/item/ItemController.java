package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание вещи " + itemDto);
        return client.addItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id) {
        return client.getItem(id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItemsForUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return client.searchItems(text);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @RequestBody ItemDto itemDto, @PathVariable Long id) {
        return client.updateItem(userId, itemDto, id);

    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable Long id) {

        return client.createComment(userId, commentDto, id);
    }
}