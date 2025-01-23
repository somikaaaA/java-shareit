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
    private Long owner;
    private Long request;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasAvailable() {
        return available != null;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean hasRequest() {
        return request != null;
    }
}



