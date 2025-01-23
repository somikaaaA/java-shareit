package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validate.DataInPastValid;
import ru.practicum.shareit.validate.ItemIdValid;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
@Builder
@Setter
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotNull(message = "Поле start не должно быть пустым")
    @DataInPastValid
    private LocalDateTime start;
    @NotNull(message = "Поле end не должно быть пустым")
    @DataInPastValid
    private LocalDateTime end;
    private Item item;
    @ItemIdValid
    private Long itemId;
    private User booker;
    private Status status;
}