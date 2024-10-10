package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;

public class FcmException extends RuntimeException {
    private final int errorCode;

    public FcmException(String message) {
        super(message);
        this.errorCode = ErrorCode.FCM_ERROR.getCode();
    }
}
