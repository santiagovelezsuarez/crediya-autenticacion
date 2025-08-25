package co.pragma.api.dto;

import co.pragma.api.exception.DtoValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    public static <T> Mono<T> validate(T dto, Validator validator) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            List<String> messages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            return Mono.error(new DtoValidationException(messages));
        }
        return Mono.just(dto);
    }
}