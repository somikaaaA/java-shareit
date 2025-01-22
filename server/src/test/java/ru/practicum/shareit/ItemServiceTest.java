package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional // Откат транзакции после каждого теста Spring автоматически откатывает транзакцию после каждого теста()
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;

    UserDto u1;
    ItemDto i1, i2;
    UpdateItemRequest request;

    @BeforeEach
    public void createItemDto() {
        u1 = UserDto.builder()
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        u1 = userService.createUser(u1);
        User user = UserMapper.toUser(u1);
        i1 = ItemDto.builder()
                .name("name i1")
                .description("description i1")
                .available(true)
                .owner(user)
                .build();
        i2 = ItemDto.builder()
                .name("name i2")
                .description("description i2")
                .available(false)
                .owner(user)
                .build();
        request = UpdateItemRequest.builder()
                .name("name request")
                .description("description request")
                .available(false)
                .owner(u1.getId())
                .build();
        i1 = itemService.createItem(u1.getId(),i1);
        i2 = itemService.createItem(u1.getId(),i2);
    }

    @Test
    public void testCreateItemInRepository() {
        assertThat(i1.getId()).isNotNull();
        assertThat(i1)
                .hasFieldOrPropertyWithValue("name", "name i1")
                .hasFieldOrPropertyWithValue("description", "description i1")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    public void testUpdateItem() {
        request.setId(i1.getId());
        ItemDto updatedItem = itemService.updateItem(request);
        assertThat(updatedItem)
                .hasFieldOrPropertyWithValue("name", "name request")
                .hasFieldOrPropertyWithValue("description", "description request")
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    public void testGetItemsForUser() {
        List<ItemDto> list = itemService.getItemsForUser(u1.getId());
        assertThat(list)
                .hasSize(2)
                .extracting(ItemDto::getName)
                .contains("name i1", "name i2");
    }

    @Test
    public void testSearchItems() {
        List<ItemDto> list = itemService.searchItems("scrip");
        assertThat(list)
                .filteredOn(ItemDto::getAvailable, true)
                .extracting(ItemDto::getDescription)
                .contains("description i1");
    }

    @Test
    public void testIsItemRegistered() {
        assertTrue(itemService.isItemRegistered(i1.getId()));
    }
}
