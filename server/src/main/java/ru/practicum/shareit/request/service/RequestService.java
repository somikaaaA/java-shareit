package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequest getRequestById(Long id);

    List<ItemRequestDto> getRequests(Long id);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getRequest(Long requestId);
}
