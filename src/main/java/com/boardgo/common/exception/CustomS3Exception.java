package com.boardgo.common.exception;

import static com.boardgo.common.exception.advice.dto.ErrorCode.S3_ERROR;

public class CustomS3Exception extends RuntimeException {
    private final int errorCode;

    public CustomS3Exception(String message) {
        super(message);
        this.errorCode = S3_ERROR.getCode();
    }
}
