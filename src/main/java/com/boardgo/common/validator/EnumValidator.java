package com.boardgo.common.validator;

import static com.boardgo.common.utils.CustomStringUtils.existString;

import com.boardgo.common.validator.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<EnumValue, String> {

    private EnumValue enumValue;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Enum<?>[] enumValues = this.enumValue.enumClass().getEnumConstants();
        if (enumValues == null || !existString(value)) {
            return false;
        }
        if (existString(enumValue.constraintEquals())
                && !value.equalsIgnoreCase(enumValue.constraintEquals())) {
            return false;
        }
        return Arrays.stream(enumValues)
                .anyMatch(enumValue -> value.equalsIgnoreCase(enumValue.toString()));
    }

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValue = constraintAnnotation;
    }
}
