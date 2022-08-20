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
import javax.xml.bind.DataBindingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private Map<ErrorType, Error> cachedError = new HashMap<>();

    // 변수 없는 에러 타입은 캐싱해서 사용한다.
    {
        cachedError.put(ErrorType.UNKNOWN, new Error(ErrorType.UNKNOWN, "unknown exception"));
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Error> handleNoDataFoundException(NoDataFoundException exception) {
        String id = exception.getMessage();
        Error error = new Error(ErrorType.NO_DATA_FOUND, "no data found exception with %s", id);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Error> handleValidationException(ValidationException exception) {
        Error error = new Error(ErrorType.NOT_VALID, exception.getMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnknownException(Exception exception) {
        log.info("call unknownException\n{}", exception.getMessage());
        Error error = cachedError.get(ErrorType.UNKNOWN);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    // @Valid 예외 바인딩 오버라이드
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(ErrorType.INVALID_PARAMETER, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    @Getter
    private static class Error {
         private ErrorType errorType;
         private String msg;
         @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
         private LocalDateTime errorTime = LocalDateTime.now();

         public Error(ErrorType errorType, String msg, Object ...args) {
             this.errorType = errorType;
             this.msg = String.format(msg, args);
         }
         public HttpStatus getHttpStatus() {
             return this.errorType.getHttpStatus();
         }
    }

    @Getter
    private enum ErrorType {
        NO_DATA_FOUND(HttpStatus.BAD_REQUEST),
        INVALID_PARAMETER(HttpStatus.BAD_REQUEST),
        NOT_VALID(HttpStatus.BAD_REQUEST),
        UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR)
        ;

        private HttpStatus httpStatus;
        ErrorType(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }
    }
}
