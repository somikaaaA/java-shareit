package ru.practicum.shareit.MockTests.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.exception.UserIdNotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private RequestRepository requestRepository;
    @MockBean
    private CommentService commentService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private ItemDto item1Dto, item2Dto, updatedItem;
    private User user;
    private UpdateItemRequest updatedRequest;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        item1Dto = ItemDto.builder()
                .id(1L)
                .name("item1 name")
                .description("item1 description")
                .available(true)
                .owner(user)
                .build();
        item2Dto = ItemDto.builder()
                .id(2L)
                .name("item2 name")
                .description("item2 description")
                .available(false)
                .owner(user)
                .build();
        updatedItem = ItemDto.builder()
                .id(1L)
                .name("item1Updated name")
                .description("item1Updated description")
                .available(true)
                .build();
        updatedRequest = UpdateItemRequest.builder()
                .name("item1Updated name")
                .description("item1Updated description")
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment")
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(itemService.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(item1Dto);
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(commentService.commentsForItem(anyLong()))
                .thenReturn(Collections.emptyList());
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(item1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1Dto.getName())))
                .andExpect(jsonPath("$.description", is(item1Dto.getDescription())));

    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(item1Dto);
        when(itemService.isItemRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(get("/items/{id}", item1Dto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1Dto.getName())))
                .andExpect(jsonPath("$.description", is(item1Dto.getDescription())));
    }

    @Test
    void getItemsForUser() throws Exception {
        when(itemService.getItemsForUser(anyLong()))
                .thenReturn(Arrays.asList(item1Dto, item2Dto));
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item1 name")))
                .andExpect(jsonPath("$[0].description", is("item1 description")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2 name")))
                .andExpect(jsonPath("$[1].description", is("item2 description")));
    }

    @Test
    void getItemsForUserWithoutXSharer() throws Exception {
        when(itemService.getItemsForUser(anyLong()))
                .thenReturn(Arrays.asList(item1Dto, item2Dto));
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Не указан заголовок X-Sharer-User-Id. " +
                        "Required request header 'X-Sharer-User-Id' for method parameter type Long is not present")));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(UpdateItemRequest.class)))
                .thenReturn(updatedItem);
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(itemService.isItemRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(patch("/items/{id}", item1Dto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(updatedRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())));
    }

    @Test
    void addComment() throws Exception {
        when(commentService.createComment(anyLong(), any(CommentDto.class), anyLong()))
                .thenReturn(commentDto);
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(itemService.isItemRegistered(anyLong()))
                .thenReturn(true);
        mvc.perform(post("/items/{id}/comment", item1Dto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void searchItemsEmptyText() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void addCommentItemNotFoundWithThrow() throws Exception {
        when(userService.isUserRegistered(anyLong()))
                .thenReturn(true);
        when(itemService.isItemRegistered(anyLong()))
                .thenThrow(new ItemIdNotFoundException("Некорректный id вещи"));

        mvc.perform(post("/items/{id}/comment", 999)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("В запросе неверно указан itemIdНекорректный id вещи")));
    }

    @Test
    void addCommentUserNotFoundWithThrow() throws Exception {
        when(userService.isUserRegistered(anyLong()))
                .thenThrow(new UserIdNotFoundException("Некорректный id пользователя"));
        when(itemService.isItemRegistered(anyLong()))
                .thenReturn(true);

        mvc.perform(post("/items/{id}/comment", 999)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("В запросе неверно указан userId Некорректный id пользователя")));
    }
}
