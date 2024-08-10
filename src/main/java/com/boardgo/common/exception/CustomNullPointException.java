package com.boardgo.common.exception;

import lombok.Getter;

@Getter
public class CustomNullPointException extends NullPointerException {
    private final int errorCode;

    public CustomNullPointException(int errorCode) {
        this.errorCode = errorCode;
    }

    public CustomNullPointException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
