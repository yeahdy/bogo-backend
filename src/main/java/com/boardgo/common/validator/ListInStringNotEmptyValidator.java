package com.boardgo.common.validator;

import java.util.List;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ListInStringNotEmptyValidator implements ConstraintValidator<ListInStringNotEmpty, List<String>> {

	@Override
	public void initialize(ListInStringNotEmpty constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(List<String> value, ConstraintValidatorContext constraintValidatorContext) {
		System.out.println(value);
		if (value == null) {
			return true; // List 자체가 null인 경우 유효성 검사를 통과
		}
		return value.stream().allMatch(element -> element != null && !element.trim().isEmpty());
	}

}
