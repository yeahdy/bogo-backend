package com.boardgo.common.exception.advice.dto;

public record FieldErrorResponse(
	String fieldName,
	String message
) {
}
