package ru.practicum.shareit.item.repository;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateItemRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long request;

    public boolean hasName() {
        return !name.isBlank();
    }

    public boolean hasDescription() {
        return !description.isBlank();
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
