package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.exception.UserIdNotFoundException;
import ru.practicum.shareit.user.service.UserService;

@RequiredArgsConstructor
public class UserIdValidator implements ConstraintValidator<UserIdValid, Long> {
    private final UserService userService;

    @Override
    public void initialize(UserIdValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if (id == 0 || !userService.isUserRegistered(id)) {
            throw new UserIdNotFoundException("Пользователь с user Id = " + id + " не найден");
        }
        return true;
    }
}
