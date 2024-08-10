package com.boardgo.common.validator;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

public class ListInStringNotEmptyValidator
        implements ConstraintValidator<ListInStringNotEmpty, List<String>> {

    @Override
    public void initialize(ListInStringNotEmpty constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(
            List<String> value, ConstraintValidatorContext constraintValidatorContext) {
        if (isNull(value)) {
            return true; // List 자체가 null인 경우 유효성 검사를 통과
        }
        return value.stream().allMatch(element -> element != null && !element.trim().isEmpty());
    }

    private boolean isNull(List<String> s) {
        return !Objects.nonNull(s);
    }
}
