package com.boardgo.common.exception;

import java.util.NoSuchElementException;
import lombok.Getter;

@Getter
public class CustomNoSuchElementException extends NoSuchElementException {
    private final int errorCode;

    public CustomNoSuchElementException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
