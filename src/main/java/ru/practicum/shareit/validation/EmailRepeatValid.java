package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailRepeatValidator.class)
// указывает, что аннотация, к которой она применяется, является аннотацией валидации
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailRepeatValid {
    String message() default "Данный e-mail уже зарегистрирован";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
