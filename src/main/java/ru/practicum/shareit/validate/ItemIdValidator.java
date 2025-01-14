package ru.practicum.shareit.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.InvalidItemIdException;
import ru.practicum.shareit.item.service.ItemService;

@RequiredArgsConstructor
public class ItemIdValidator implements ConstraintValidator<ItemIdValid, Long> {
    private final ItemService itemService;

    @Override
    public void initialize(ItemIdValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if (id == 0 || !itemService.isItemRegistered(id)) {
            throw new InvalidItemIdException("Вещь с itemId " + id + " не найдена в базе");
        }
        return true;
    }
}
