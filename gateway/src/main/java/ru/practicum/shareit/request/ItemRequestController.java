package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создание запроса пользователем {}, с описанием {}", userId, itemRequestDto.getDescription());
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Запросы для пользователя с id" + userId);
        return client.getRequests(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long id) {
        log.info("Поиск запроса с id" + id);
        return client.getRequestById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        log.info("Поиск всех запросов");
        return client.getAllRequests();
    }
}