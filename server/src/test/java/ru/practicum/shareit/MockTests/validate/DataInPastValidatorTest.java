package ru.practicum.shareit.MockTests.validate;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.validation.DataInPastValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DataInPastValidatorTest {
    private DataInPastValidator dataInPastValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInPastValidator = new DataInPastValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValidWhenDateIsNullReturnsTrue() {
        LocalDateTime date = null;
        boolean result = dataInPastValidator.isValid(date, context);
        assertTrue(result);
    }

    @Test
    void isValidWhenDateIsInFutureReturnsTrue() {
        LocalDateTime date = LocalDateTime.now().plusDays(1); // Дата в будущем
        boolean result = dataInPastValidator.isValid(date, context);
        assertTrue(result); // Ожидаем, что метод вернет true
    }

    @Test
    void isValid_WhenDateIsInPast_ReturnsFalse() {
        LocalDateTime date = LocalDateTime.now().minusDays(1); // Дата в прошлом
        boolean result = dataInPastValidator.isValid(date, context);
        assertFalse(result); // Ожидаем, что метод вернет false
    }
}
