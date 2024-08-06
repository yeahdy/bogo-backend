package com.boardgo.common.exception;

import lombok.Getter;

@Getter
public class CustomIllegalArgumentException extends IllegalArgumentException {
    private final int errorCode;

    public CustomIllegalArgumentException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
