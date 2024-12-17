package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //Свойство может быть только для чтения. Запрет на изменение через JSon
    private Long id;
    @NotBlank(message = "Поле name должно быть заполнено")
    private String name;
    @NotBlank(message = "Поле description должно быть заполнено")
    private String description;
    @NotNull(message = "Поле available должно быть заполнено")
    private Boolean available;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long owner;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long request;
}
