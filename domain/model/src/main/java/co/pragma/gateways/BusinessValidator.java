package co.pragma.gateways;

import reactor.core.publisher.Mono;

public interface BusinessValidator<T> {
    Mono<T> validate(T model);
}
