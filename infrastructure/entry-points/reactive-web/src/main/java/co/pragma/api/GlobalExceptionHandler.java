package co.pragma.api;

import co.pragma.base.exception.BusinessException;
import co.pragma.base.exception.EmailAlreadyRegisteredException;
import common.api.dto.ErrorResponse;
import common.api.exception.DtoValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex, ServerHttpRequest request) {
        log.info("EmailAlreadyRegisteredException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("EMAIL_ALREADY_REGISTERED")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException ex, ServerHttpRequest request) {
        log.warn("BusinessException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(java.time.Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BUSINESS_ERROR")
                .message(ex.getMessage())
                .path(request.getPath().value())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

    @ExceptionHandler(DtoValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(DtoValidationException ex, ServerHttpRequest request) {
        log.info("DtoValidationException: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getErrors().stream()
                .map(err -> new ErrorResponse.FieldError(err.field(), err.message()))
                .toList();

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Existen errores de validación")
                .path(request.getPath().value())
                .validationErrors(fieldErrors)
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));

    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(java.time.Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred.")
                .path("")
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error));
    }
}

