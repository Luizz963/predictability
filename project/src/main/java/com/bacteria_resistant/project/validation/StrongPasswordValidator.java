package com.bacteria_resistant.project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        return value.length() >= 8
                && value.matches(".*[A-Z].*")          // maiúscula
                && value.matches(".*[a-z].*")          // minúscula
                && value.matches(".*\\d.*")            // número
                && value.matches(".*[^A-Za-z0-9].*");  // símbolo
    }
}