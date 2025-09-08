package co.pragma.api.exception;

import co.pragma.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ErrorResponseFactory {

    private ErrorResponseFactory() { }

    public static ErrorResponse from(ErrorContext ctx) {
        return from(ctx.request(), ctx.status(), ctx.errorCode(), ctx.message(), ctx.fieldErrors());
    }

    private static ErrorResponse from(ServerRequest request,
                                     HttpStatus status,
                                     String errorCode,
                                     String message,
                                     List<ErrorResponse.FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .path(request.path())
                .error(errorCode)
                .message(message)
                .fieldErrors(Optional.ofNullable(fieldErrors).orElse(List.of()))
                .build();
    }
}
