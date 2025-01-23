package ru.practicum.shareit.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DataInPastValidator implements ConstraintValidator<DataInPastValid, LocalDateTime> {
    @Override
    public void initialize(DataInPastValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return date.isAfter(LocalDateTime.now());

    }
}
