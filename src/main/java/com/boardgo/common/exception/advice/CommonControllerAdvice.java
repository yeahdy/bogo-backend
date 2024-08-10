package com.boardgo.common.exception.advice;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.advice.dto.ErrorCode;
import com.boardgo.common.exception.advice.dto.ErrorResponse;
import com.boardgo.common.exception.advice.dto.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class CommonControllerAdvice {
    /** Entity Validation Error */
    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> ConstraintViolationException() {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.BAD_REQUEST.getCode())
                                .messages(ErrorCode.BAD_REQUEST.getMessage())
                                .build());
    }

    /** Request Dto Validation Error */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validExHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldErrorResponse> fieldErrorResponses =
                bindingResult.getFieldErrors().stream()
                        .map(
                                item ->
                                        new FieldErrorResponse(
                                                item.getField(), item.getDefaultMessage()))
                        .toList();

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.BAD_REQUEST.getCode())
                                .messages(FieldErrorResponse.listToString(fieldErrorResponses))
                                .build());
    }

    /** Client에서 JSON Format에 맞지 않게 보내서 파싱이 실패했을 때 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> JsonParseExHandler(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.JSON_PARSING_ERROR.getCode())
                                .messages(ErrorCode.JSON_PARSING_ERROR.getMessage())
                                .build());
    }

    /** 이미 존재하는 값일 경우 * */
    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> CustomIllegalArgumentException(
            CustomIllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(exception.getErrorCode())
                                .messages(exception.getMessage())
                                .build());
    }

    /*************** NotFound */
    @ExceptionHandler(CustomNoSuchElementException.class)
    public ResponseEntity<ErrorResponse> customNoSuchElementException(
            CustomNoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(
                        ErrorResponse.builder()
                                .errorCode(exception.getErrorCode())
                                .messages(exception.getMessage())
                                .build());
    }

    /** 데이터가 존재하지 않을 경우 * */
    @ExceptionHandler(CustomNullPointException.class)
    public ResponseEntity<ErrorResponse> CustomNullPointException(
            CustomNullPointException exception) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(exception.getErrorCode())
                                .messages(exception.getMessage())
                                .build());
    }
}
