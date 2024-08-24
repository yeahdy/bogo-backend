package com.boardgo.common.exception;

import static com.boardgo.common.exception.advice.dto.ErrorCode.JSON_PARSING_ERROR;

public class CustomJsonProcessingException extends RuntimeException {
    private final int errorCode;

    public CustomJsonProcessingException(String message) {
        super(message);
        this.errorCode = JSON_PARSING_ERROR.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
