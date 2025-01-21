package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.service.UserService;

@RequiredArgsConstructor

public class EmailRepeatValidator implements ConstraintValidator<EmailRepeatValid, String> {
    private final UserService userService;

    @Override
    public void initialize(EmailRepeatValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (userService.isEmailRegistered(email)) {
            throw new InvalidEmailException("Данный email " + email + " уже существует в базе");
        }
        return true;
    }
}
