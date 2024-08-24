package com.boardgo.common.exception;

import com.boardgo.common.exception.advice.dto.ErrorCode;
import java.util.NoSuchElementException;
import lombok.Getter;

@Getter
public class CustomNoSuchElementException extends NoSuchElementException {
    private final int errorCode;

    public CustomNoSuchElementException(String message) {
        super(message + " (이)가 존재하지 않습니다.");
        this.errorCode = ErrorCode.ELEMENT_NOT_FOUND.getCode();
    }
}
