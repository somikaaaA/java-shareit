package ru.practicum.shareit.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataInPastValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataInPastValid {
    String message() default "Дата не может быть в прошлом";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
