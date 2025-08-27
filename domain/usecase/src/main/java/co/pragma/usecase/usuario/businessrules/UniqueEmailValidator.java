package co.pragma.usecase.usuario.businessrules;

import co.pragma.base.exception.EmailAlreadyRegisteredException;
import co.pragma.model.usuario.Usuario;
import co.pragma.base.gateways.BusinessValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UniqueEmailValidator implements BusinessValidator<Usuario> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<Usuario> validate(Usuario usuario) {
        return usuarioRepository.findByEmail(usuario.getEmail())
                .flatMap(found -> Mono.<Usuario>error(new EmailAlreadyRegisteredException("El correo ya está registrado")))
                .switchIfEmpty(Mono.just(usuario));
    }

}

