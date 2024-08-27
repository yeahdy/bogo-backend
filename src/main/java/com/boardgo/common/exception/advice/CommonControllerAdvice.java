package com.boardgo.common.exception.advice;

import static com.boardgo.common.exception.advice.dto.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.boardgo.common.exception.advice.dto.ErrorCode.UNSUPPORTED_HTTP_METHOD;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.advice.dto.ErrorCode;
import com.boardgo.common.exception.advice.dto.ErrorResponse;
import com.boardgo.common.exception.advice.dto.FieldErrorResponse;
import com.boardgo.config.log.OutputLog;
import jakarta.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class CommonControllerAdvice {
    /** Entity Validation Error HTTP Method Type Error */
    @ExceptionHandler({
        HandlerMethodValidationException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> ConstraintViolationException(Exception e) {
        OutputLog.logError(getPrintStackTrace(e));
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> constraintValidExHandler(ConstraintViolationException e) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCode.BAD_REQUEST.getCode())
                                .messages(e.getMessage())
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

    /** 메서드에서 지원하지 않는 형식으로 서버가 요청을 거부할 경우 */
    @ExceptionHandler({
        HttpMediaTypeNotSupportedException.class,
        HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> HttpMediaTypeNotSupportedException() {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(
                        ErrorResponse.builder()
                                .errorCode(UNSUPPORTED_HTTP_METHOD.getCode())
                                .messages(UNSUPPORTED_HTTP_METHOD.getMessage())
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
    public ResponseEntity<ErrorResponse> customNullPointException(
            CustomNullPointException exception) {
        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .errorCode(exception.getErrorCode())
                                .messages(exception.getMessage())
                                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception exception) {
        OutputLog.logError(getPrintStackTrace(exception));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .errorCode(INTERNAL_SERVER_ERROR.getCode())
                                .messages(INTERNAL_SERVER_ERROR.getMessage())
                                .build());
    }

    private String getPrintStackTrace(Exception e) {
        StringWriter error = new StringWriter();
        e.printStackTrace(new PrintWriter(error));
        return error.toString();
    }
}
