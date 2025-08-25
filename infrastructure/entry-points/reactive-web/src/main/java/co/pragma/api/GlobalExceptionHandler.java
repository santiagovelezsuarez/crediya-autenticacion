package co.pragma.api;

import co.pragma.api.exception.DtoValidationException;
import co.pragma.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException ex) {
        log.warn("BusinessException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

    @ExceptionHandler(DtoValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(DtoValidationException ex) {
        log.info("DtoValidationException: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error));
    }

//    @ExceptionHandler(Exception.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleGeneralException(Exception ex) {
//        log.error("Unexpected error: ", ex);
//        ErrorResponse error = new ErrorResponse("Ha ocurrido un error inesperado");
//        return Mono.just(ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(error));
//    }
}

