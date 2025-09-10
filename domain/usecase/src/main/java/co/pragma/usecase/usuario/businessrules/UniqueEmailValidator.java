package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.business.EmailAlreadyRegisteredException;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UniqueEmailValidator {

    private final UsuarioRepository usuarioRepository;

    public Mono<Void> validate(String email) {
        return usuarioRepository.findByEmail(email)
                .flatMap(u -> Mono.error(new EmailAlreadyRegisteredException()))
                .switchIfEmpty(Mono.empty())
                .then();
    }
}

