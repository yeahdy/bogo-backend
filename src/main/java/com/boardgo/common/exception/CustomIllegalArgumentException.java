package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;
import lombok.Getter;

@Getter
public class CustomIllegalArgumentException extends IllegalArgumentException {
    private final int errorCode;

    public CustomIllegalArgumentException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CustomIllegalArgumentException(String message) {
        super(message);
        this.errorCode = ErrorCode.BAD_REQUEST.getCode();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
