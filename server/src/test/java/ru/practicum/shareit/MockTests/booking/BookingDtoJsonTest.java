package ru.practicum.shareit.MockTests.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 2, 12, 12, 0, 0))
                .end(LocalDateTime.of(2025, 2, 17, 12, 0, 0))
                .itemId(2L)
                .booker(User.builder()
                        .id(2L)
                        .name("name b1")
                        .email("b1@mail.ru")
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("item name")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("name u1")
                                .email("u1@mail.ru")
                                .build())
                        .build())
                .build();
        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-02-12T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-02-17T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name b1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("b1@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("name u1");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("u1@mail.ru");
//поля присутствуют в JSON после сериализации.
        assertThat(result.getJson()).contains("\"id\":1");
        assertThat(result.getJson()).contains("\"start\":\"2025-02-12T12:00:00\"");
        assertThat(result.getJson()).contains("\"end\":\"2025-02-17T12:00:00\"");

    }

    @Test
    void deserializeBookingDto() throws IOException {
        String jsonString = "{" +
                "\"id\": 1," +
                "\"start\": \"2025-02-12T12:00:00\"," +
                "\"end\": \"2025-02-17T12:00:00\"," +
                "\"itemId\": 2," +
                "\"booker\": {" +
                "\"id\": 2," +
                "\"name\": \"name b1\"," +
                "\"email\": \"b1@mail.ru\"" +
                "}," +
                "\"item\": {" +
                "\"id\": 1," +
                "\"name\": \"item1 name\"," +
                "\"description\": \"item1 description\"," +
                "\"available\": true," +
                "\"owner\": {" +
                "\"id\": 1," +
                "\"name\": \"name u1\"," +
                "\"email\": \"u1@mail.ru\"" +
                "}" +
                "}" +
                "}";
        BookingDto deserializedBookingDto = json.parse(jsonString).getObject();

        assertNull(deserializedBookingDto.getId()); //readOnly
        assertEquals(LocalDateTime.of(2025, 2, 12, 12, 0, 0), deserializedBookingDto.getStart());
        assertEquals(LocalDateTime.of(2025, 2, 17, 12, 0, 0), deserializedBookingDto.getEnd());
        assertEquals(2L, deserializedBookingDto.getItemId());

        User booker = deserializedBookingDto.getBooker();
        assertEquals(2L, booker.getId());
        assertEquals("name b1", booker.getName());
        assertEquals("b1@mail.ru", booker.getEmail());

        Item item = deserializedBookingDto.getItem();
        assertEquals(1L, item.getId());
        assertEquals("item1 name", item.getName());
        assertEquals("item1 description", item.getDescription());
        assertTrue(item.getAvailable());

        User owner = item.getOwner();
        assertEquals(1L, owner.getId());
        assertEquals("name u1", owner.getName());
        assertEquals("u1@mail.ru", owner.getEmail());
    }
}
