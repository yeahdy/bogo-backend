package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;

public class DuplicateException extends CustomIllegalArgumentException {

    private final int errorCode;

    public DuplicateException(String message) {
        super(message);
        this.errorCode = ErrorCode.DUPLICATE_DATA.getCode();
    }
}
