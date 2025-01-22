package ru.practicum.shareit.MockTests.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.RequestIdNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User  1")
                .email("user1@example.com")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need Item")
                .requester(user)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need Item")
                .requester(user)
                .build();
    }

    @Test
    void createRequest() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDto result = requestService.createRequest(itemRequestDto, user.getId());
        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(userService).getUserById(user.getId());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getRequestByIdWhenRequestExists() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequest result = requestService.getRequestById(itemRequest.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(requestRepository).findById(itemRequest.getId());
    }

    @Test
    void getRequestByIdWhenRequestDoesNotExist() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        RequestIdNotFoundException exception = assertThrows(RequestIdNotFoundException.class, () -> {
            requestService.getRequestById(999L);
        });
        assertEquals("Запрос с id 999 не найден", exception.getMessage());
        verify(requestRepository).findById(999L);
    }

    @Test
    void getRequests() {
        when(requestRepository.findByRequester(anyLong())).thenReturn(List.of(itemRequest));
        when(itemService.searchItemByRequest(anyLong())).thenReturn(Collections.emptyList());
        List<ItemRequestDto> result = requestService.getRequests(user.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());
        verify(requestRepository).findByRequester(user.getId());
    }

    @Test
    void getAllRequests() {
        when(requestRepository.findAll()).thenReturn(List.of(itemRequest));
        when(itemService.searchItemByRequest(anyLong())).thenReturn(Collections.emptyList());
        List<ItemRequestDto> result = requestService.getAllRequests();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());
        verify(requestRepository).findAll();
    }

    @Test
    void getRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.searchItemByRequest(anyLong())).thenReturn(Collections.emptyList());
        ItemRequestDto result = requestService.getRequest(itemRequest.getId());
        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());
        verify(requestRepository).findById(itemRequest.getId());
    }
}
