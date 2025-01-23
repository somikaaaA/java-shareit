package ru.practicum.shareit.MockTests.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.stateStrategy.Status;
import ru.practicum.shareit.exception.BookingIdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemService itemService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;
    private BookingDto bookingDto1, bookingDto2;
    private Booking booking;
    private Item item1, item2;
    private User user, booker;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule()); //для преобразования LocalDateTime
        user = User.builder()
                .id(1L)
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        booker = User.builder()
                .id(2L)
                .name("name b1")
                .email("b1@mail.ru")
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .owner(user)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("item2 name")
                .description("item2 description")
                .available(true)
                .owner(user)
                .build();
        bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 11, 10, 12, 00, 00))
                .end(LocalDateTime.of(2025, 11, 15, 12, 00, 00))
                .itemId(1L)
                .booker(booker)
                .item(item1)
                .build();
        bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2025, 12, 12, 12, 00, 00))
                .end(LocalDateTime.of(2025, 12, 17, 12, 00, 00))
                .itemId(2L)
                .booker(booker)
                .item(item2)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 12, 12, 12, 00, 00))
                .end(LocalDateTime.of(2025, 12, 17, 12, 00, 00))
                .booker(booker)
                .item(item1)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void addBooking() throws Exception {

        when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto1);
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(itemService.isItemRegistered(anyLong()))
                .thenReturn(true);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));


        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())));

    }

    @Test
    void searchBookingForOwner() throws Exception {
        when(bookingService.searchBookingsForOwner(anyLong()))
                .thenReturn(Arrays.asList(bookingDto1, bookingDto2));
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().format(formatter))))
                .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().format(formatter))));
    }

    @Test
    void searchBookingByIdForOwnerOrForBooker() throws Exception {
        when(bookingService.searchBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto1);
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(get("/bookings/{bookingId}", bookingDto1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())));
    }

    @Test
    void getInvalidBooking() throws Exception {
        when(bookingRepository.findById(anyLong())).thenThrow(BookingIdNotFoundException.class);
        mvc.perform(get("/bookings/{bookingId}", bookingDto1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void addApprove() throws Exception {
        when(bookingService.createApprove(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto1.getId())
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())));
    }

    @Test
    void searchBookingForUserWithState() throws Exception {
        when(bookingService.searchBookingsWithState(anyLong(), any(Status.class)))
                .thenReturn(Arrays.asList(bookingDto1, bookingDto2));
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);

        mvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
}
