package com.boardgo.common.exception;

import static com.boardgo.common.exception.advice.dto.ErrorCode.OAUTH2_ERROR;

public class OAuth2Exception extends CustomIllegalArgumentException {

    public OAuth2Exception(String message) {
        super(OAUTH2_ERROR.getCode(), message);
    }
}
