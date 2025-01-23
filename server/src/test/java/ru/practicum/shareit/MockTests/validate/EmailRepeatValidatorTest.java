package ru.practicum.shareit.MockTests.validate;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.EmailRepeatValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailRepeatValidatorTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private EmailRepeatValidator emailRepeatValidator;

    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValidWhenEmailIsRegisteredThrowsInvalidEmailException() {

        String email = "test@mail.ru";
        when(userService.isEmailRegistered(email)).thenReturn(true);

        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            emailRepeatValidator.isValid(email, context);
        });

        assertEquals("Данный email " + email + " уже существует в базе", exception.getMessage());
        verify(userService, times(1)).isEmailRegistered(email);
    }

    @Test
    void isValidWhenEmailIsNotRegisteredReturnsTrue() {

        String email = "test@mail.ru";
        when(userService.isEmailRegistered(email)).thenReturn(false);
        boolean result = emailRepeatValidator.isValid(email, context);
        assertTrue(result);
        verify(userService, times(1)).isEmailRegistered(email);
    }
}
