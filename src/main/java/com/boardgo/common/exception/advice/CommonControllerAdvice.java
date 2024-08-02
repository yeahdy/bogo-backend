package com.boardgo.common.exception.advice;

import com.boardgo.common.exception.advice.dto.ErrorResponse;
import com.boardgo.common.exception.advice.dto.FieldErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonControllerAdvice {
    /** Entity Validation Error */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> ConstraintViolationException() {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder().errorCode(400).messages("Validation Error").build());
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
                                .errorCode(400)
                                .messages(FieldErrorResponse.listToString(fieldErrorResponses))
                                .build());
    }

    /** Client에서 JSON Format에 맞지 않게 보내서 파싱이 실패했을 때 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> JsonParseExHandler(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(400)
                                .messages("JSON Parsing Error")
                                .build());
    }
}
