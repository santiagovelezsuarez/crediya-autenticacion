package co.pragma.api.dto;

import co.pragma.api.exception.DtoValidationException;
import co.pragma.api.exception.DtoValidationException.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static <T> Mono<T> validate(T dto, Validator validator) {
        Set<ConstraintViolation<T>> fieldErrors = validator.validate(dto);

        if (!fieldErrors.isEmpty()) {
            List<FieldError> errors = fieldErrors.stream()
                    .map(attr -> new DtoValidationException.FieldError(
                            attr.getPropertyPath().toString(),
                            attr.getMessage()
                    ))
                    .toList();

            return Mono.error(new DtoValidationException(errors));
        }

        return Mono.just(dto);
    }
}
