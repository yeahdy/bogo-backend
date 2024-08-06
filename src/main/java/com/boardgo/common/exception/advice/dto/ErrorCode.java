package com.boardgo.common.exception.advice.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNSUPPORTED_HTTP_METHOD(4000, "요청한 HTTP 메소드는 지원되지 않습니다"),
    JSON_PARSING_ERROR(4001, "JSON 데이터 파싱 중 오류가 발생했습니다"),
    DUPLICATE_DATA(4002, "중복된 데이터 입니다"),
    UNAUTHORIZED(401, "권한이 존재하지 않습니다"),
    FORBIDDEN(403, "권한이 적절하지 않습니다"),
    NOT_FOUND(404, "존재하지 않는 URI 입니다"),
    INTERNAL_SERVER_ERROR(500, "알 수 없는 서버 에러 입니다");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
