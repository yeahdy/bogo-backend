package com.boardgo.common.exception.advice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
	int errorCode,
	String messages
) {
}
