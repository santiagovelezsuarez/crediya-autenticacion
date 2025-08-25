package co.pragma.usecase.user.criteria;

import co.pragma.model.usuario.Usuario;
import co.pragma.common.gateways.BusinessValidator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Objects;

public class ValidationChain implements BusinessValidator<Usuario> {

    private final List<BusinessValidator<Usuario>> validators;

    public ValidationChain(List<BusinessValidator<Usuario>> validators) {
        this.validators = Objects.requireNonNull(validators);
    }

    @Override
    public Mono<Usuario> validate(Usuario usuario) {
        return Flux.fromIterable(validators)
                .flatMap(validator -> validator.validate(usuario))
                .then(Mono.just(usuario));
    }
}
