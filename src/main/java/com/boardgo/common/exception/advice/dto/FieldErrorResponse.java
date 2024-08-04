package com.boardgo.common.exception.advice.dto;

import java.util.List;
import java.util.stream.Collectors;

public record FieldErrorResponse(String fieldName, String message) {

    @Override
    public String toString() {
        return "fieldName=" + fieldName + ", message=" + message;
    }

    public static String listToString(List<FieldErrorResponse> errors) {
        return errors.stream()
                .map(FieldErrorResponse::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
