package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Поле name должно быть запонено")
    private String name;
    @NotBlank(message = "Поле description должно быть запонено")
    private String description;
    @NotBlank(message = "Поле available должно быть запонено")
    private Boolean available;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long ownerId; // владелец
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long request;
}
