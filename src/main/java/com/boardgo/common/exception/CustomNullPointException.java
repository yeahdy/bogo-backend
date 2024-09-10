package com.boardgo.common.exception;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import lombok.Getter;

@Getter
public class CustomNullPointException extends NullPointerException {
    private final int errorCode;

    public CustomNullPointException(String message) {
        super(message);
        this.errorCode = NULL_ERROR.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
