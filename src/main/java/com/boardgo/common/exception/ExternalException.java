package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;

public class ExternalException extends RuntimeException {
    private final ErrorCode errorCode;

    public ExternalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ExternalException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
