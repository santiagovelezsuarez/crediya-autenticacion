package co.pragma.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class DtoValidationException extends RuntimeException {
    private final List<FieldError> errors;

    public DtoValidationException(List<FieldError> errors) {
        super("Error en la petición, verifique los campos");
        this.errors = errors;
    }

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String message;
    }
}