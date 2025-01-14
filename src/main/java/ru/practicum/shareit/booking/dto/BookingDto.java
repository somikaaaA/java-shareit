package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.stateStrategy.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validate.DataInPastValid;
import ru.practicum.shareit.validate.ItemIdValid;

import java.time.LocalDateTime;

@Data
@Builder
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long itemId;
    private User booker;
    private Status status;
}
