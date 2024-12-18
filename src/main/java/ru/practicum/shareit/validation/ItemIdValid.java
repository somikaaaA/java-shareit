package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ItemIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemIdValid {
    String message() default "Данный id item отсутствует в базе данных";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
