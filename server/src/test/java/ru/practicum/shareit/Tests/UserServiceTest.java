package ru.practicum.shareit.Tests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UpdateUserRequest;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceTest {
    private final UserService userService;
    UserDto u1, u2, createdUserU1, createdUserU2;
    UpdateUserRequest request, requestWithRepeatEmail;

    @BeforeEach
    public void createUserDto() {
        u1 = UserDto.builder()
                .name("name u1")
                .email("u1@mail.ru")
                .build();
        u2 = UserDto.builder()
                .name("name u2")
                .email("u2@mail.ru")
                .build();
        request = UpdateUserRequest.builder()
                .name("name request")
                .email("request@mail.ru")
                .build();
        requestWithRepeatEmail = UpdateUserRequest.builder()
                .name("name request")
                .email("u2@mail.ru")
                .build();
        createdUserU1 = userService.createUser(u1);
        createdUserU2 = userService.createUser(u2);
    }


    @Test
    public void testCreateUserInRepository() {
        assertThat(createdUserU1.getId()).isNotNull();
        assertThat(createdUserU1)
                .hasFieldOrPropertyWithValue("name", "name u1")
                .hasFieldOrPropertyWithValue("email", "u1@mail.ru");
    }

    @Test
    public void testUpdateUserInRepository() {
        request.setId(createdUserU1.getId());
        UserDto updatedUser = userService.updateUser(request);
        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("name", "name request")
                .hasFieldOrPropertyWithValue("email", "request@mail.ru");
    }

    @Test
    public void testUpdateUserWithRepeatEmail() {
        requestWithRepeatEmail.setId(createdUserU1.getId());
        Exception e = assertThrows(InvalidEmailException.class, () ->
                        userService.updateUser(requestWithRepeatEmail),
                "Метод работает некорректно");
        assertTrue(e.getMessage().contains("Данный e-mail уже зарегистрирован"));
    }

    @Test
    public void testDeleteUser() {
        assertTrue(userService.isUserRegistered(createdUserU1.getId()),
                "Пользователь не найден. Метод работает некорректно");
        userService.deleteUser(createdUserU1.getId());
        assertFalse(userService.isUserRegistered(createdUserU1.getId()),
                "Пользователь найден. Метод работает некорректно");

    }

    @Test
    public void testCheckEmailRegistered() {
        assertTrue(userService.isEmailRegistered(createdUserU2.getEmail()),
                "Email не совпадает. Метод работает некорректно");
    }

    @Test
    public void testCheckUserRegistered() {
        assertTrue(userService.isUserRegistered(createdUserU1.getId()),
                "Пользователь не найден. Метод работает некорректно");

    }


}
