package co.pragma.usecase.usuario.criteria.validators;

import co.pragma.model.usuario.Usuario;
import co.pragma.common.exception.BusinessException;
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
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException("El correo ya está registrado"))
                        : Mono.just(usuario));
    }
}

