package ru.practicum.shareit.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailRepeatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailRepeatValid {
    String message() default "Данный e-mail уже зарегистрирован";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
