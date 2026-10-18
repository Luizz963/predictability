package com.bacteria_resistant.project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "A senha deve ter no mínimo 8 caracteres, com maiúscula, minúscula, número e símbolo.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}