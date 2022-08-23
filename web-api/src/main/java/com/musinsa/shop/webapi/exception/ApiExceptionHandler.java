package com.musinsa.shop.webapi.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElementException(NoSuchElementException exception) {
        log.error("call noSuchElementException\ninput id is {}", exception.getMessage());
        String id = exception.getMessage();
        return ResponseEntityHelper.ofError(Error.of(ErrorType.NO_DATA_FOUND, "no data found exception with %s", id));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleValidationException(ValidationException exception) {
        log.error("call validationException\n{}", exception.getMessage());
        return ResponseEntityHelper.ofError(Error.of(ErrorType.NOT_VALID, exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnknownException(Exception exception) {
        log.error("call unknownException\n{}", exception.getMessage());
        return ResponseEntityHelper.ofError(Error.of(ErrorType.UNKNOWN, "unknown exception"));
    }

    // @Valid 예외 바인딩 오버라이드
    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String firstMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("call methodArgumentNotValidException\n{}", firstMsg);
        return ResponseEntityHelper.ofError(Error.of(ErrorType.INVALID_PARAMETER, firstMsg));
    }

    @Getter
    private static class Error {
        private ErrorType errorType;
        private String msg;
        @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
        private LocalDateTime errorTime = LocalDateTime.now();

        public static Error of(ErrorType errorType, String msg, Object... args) {
            return new Error(errorType, msg, args);
        }

        private Error(ErrorType errorType, String msg, Object... args) {
            this.errorType = errorType;
            this.msg = String.format(msg, args);
        }

        public HttpStatus getHttpStatus() {
            return this.errorType.getHttpStatus();
        }
    }

    private static class ResponseEntityHelper {
        public static ResponseEntity ofError(Error error) {
            return ResponseEntity.status(error.getHttpStatus()).body(error);
        }
    }

    @Getter
    private enum ErrorType {
        NO_DATA_FOUND(HttpStatus.BAD_REQUEST),
        INVALID_PARAMETER(HttpStatus.BAD_REQUEST),
        NOT_VALID(HttpStatus.BAD_REQUEST),
        UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR);

        private HttpStatus httpStatus;

        ErrorType(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }
    }
}
