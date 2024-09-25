package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;

public class NotificationException extends CustomIllegalArgumentException {
    private final int errorCode;

    public NotificationException(String message) {
        super(message);
        this.errorCode = ErrorCode.NOTIFICATION_ERROR.getCode();
    }
}
