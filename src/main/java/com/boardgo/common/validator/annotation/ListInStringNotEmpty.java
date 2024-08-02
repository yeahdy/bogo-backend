package com.boardgo.common.validator.annotation;

import com.boardgo.common.validator.ListInStringNotEmptyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ListInStringNotEmptyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListInStringNotEmpty {
    String message() default "List에 Null이거나 공백인 것이 존재합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
