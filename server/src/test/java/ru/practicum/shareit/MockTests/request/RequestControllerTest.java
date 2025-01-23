package ru.practicum.shareit.MockTests.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.RequestIdNotFoundException;
import ru.practicum.shareit.exception.UserIdNotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private RequestService requestService;
    @MockBean
    private RequestRepository requestRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private User requester;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        requester = User.builder()
                .id(1L)
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("request description")
                .requester(requester)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request description")
                .requester(requester)
                .build();
    }

    @Test
    void createRequest() throws Exception {
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(requestService.createRequest(any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

    }

    @Test
    void getRequestById() throws Exception {

        when(requestService.getRequest(anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequests()).thenReturn(Collections.singletonList(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getRequestByIdNotFound() throws Exception {
        when(requestService.getRequest(anyLong())).thenThrow(new RequestIdNotFoundException("Запрос с id 1 не найден"));

        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("В запросе неверно указан requestIdЗапрос с id 1 не найден")));
    }

    @Test
    void getRequestsInvalidUserId() throws Exception {
        when(requestService.getRequests(anyLong())).thenThrow(new UserIdNotFoundException("Пользователь не найден"));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("В запросе неверно указан userId Пользователь с userId= 999не найден")));
    }

    @Test
    void getRequests() throws Exception {
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(requestService.getRequests(anyLong())).thenReturn(Collections.singletonList(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())));
    }
}
