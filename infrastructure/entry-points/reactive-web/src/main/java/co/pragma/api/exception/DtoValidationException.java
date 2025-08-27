package co.pragma.api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class DtoValidationException extends RuntimeException {
    private final List<FieldError> errors;

    public DtoValidationException(List<FieldError> errors) {
        super("Error en la petición, verifique los campos");
        this.errors = errors;
    }

    public record FieldError(String field, String message) {
    }
}