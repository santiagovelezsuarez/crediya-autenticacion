package co.pragma.api.exception;

import java.util.List;

public class DtoValidationException extends RuntimeException {

    private final List<String> errors;

    public DtoValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

