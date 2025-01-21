package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {
    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(request.getRequester())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequest toRequest(ItemRequestDto requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }
}
