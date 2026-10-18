package com.bacteria_resistant.project.validation;

import com.bacteria_resistant.project.util.CnpjUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CnpjValidator implements ConstraintValidator<Cnpj, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        return CnpjUtils.isValid(value);
    }
}