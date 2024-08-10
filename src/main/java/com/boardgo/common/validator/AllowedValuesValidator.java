package com.boardgo.common.validator;

import com.boardgo.common.validator.annotation.AllowedValues;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {

    private String[] allowedValues;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        this.allowedValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (isNull(value)) {
            return true;
        }
        return Arrays.asList(allowedValues).contains(value);
    }

    private boolean isNull(String s) {
        return !Objects.nonNull(s);
    }
}
