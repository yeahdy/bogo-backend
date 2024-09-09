package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;
import lombok.Getter;

@Getter
public class CustomUnAuthorizedException extends RuntimeException {
    private final int errorCode;

    public CustomUnAuthorizedException(String message) {
        super(message);
        errorCode = ErrorCode.UNAUTHORIZED.getCode();
    }
}
