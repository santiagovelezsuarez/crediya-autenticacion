package co.pragma.api.exception;

import co.pragma.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

public record ErrorContext(
        ServerRequest request,
        HttpStatus status,
        String errorCode,
        String message,
        List<ErrorResponse.FieldError> fieldErrors
) {
    public static ErrorContext of(ServerRequest request,
                                  HttpStatus status,
                                  String errorCode,
                                  String message) {
        return new ErrorContext(request, status, errorCode, message, List.of());
    }

    public static ErrorContext ofValidation(ServerRequest request,
                                            HttpStatus status,
                                            String errorCode,
                                            String message,
                                            List<ErrorResponse.FieldError> fieldErrors) {
        return new ErrorContext(request, status, errorCode, message, fieldErrors);
    }
}
