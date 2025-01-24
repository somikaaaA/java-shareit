package ru.practicum.shareit.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserIdValid {
    String message() default "Данный id user отсутствует в базе данных";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
