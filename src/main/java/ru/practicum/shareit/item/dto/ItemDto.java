package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //Свойство может быть только для чтения. Запрет на изменение через JSon
    private Long id;
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    private String description;
    @NotNull(message = "Поле available обязательно для указания")
    private Boolean available;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //аннотация указывает,
    // что поле будет доступно только для записи (т.е. для десериализации из JSON), но не будет включено в JSON при сериализации.
    private User owner; //владелец вещи
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;

}
