package co.pragma.usecase.usuario.businessrules;

import co.pragma.common.exception.EmailAlreadyRegisteredException;
import co.pragma.model.usuario.Usuario;
import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UniqueEmailValidator implements BusinessValidator<Usuario> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<Usuario> validate(Usuario usuario) {
        return usuarioRepository.findByEmail(usuario.getEmail())
                .hasElement()
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.error(new EmailAlreadyRegisteredException("El correo ya está registrado"))
                        : Mono.just(usuario));
    }
}

