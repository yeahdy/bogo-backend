package com.boardgo.common.exception;

import static com.boardgo.common.exception.advice.dto.ErrorCode.BAD_OAUTH2;

public class OAuth2Exception extends CustomIllegalArgumentException {

    public OAuth2Exception(String message) {
        super(BAD_OAUTH2.getCode(), message);
    }
}
