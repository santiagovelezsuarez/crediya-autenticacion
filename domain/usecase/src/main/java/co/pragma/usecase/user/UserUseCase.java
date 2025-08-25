package co.pragma.usecase.user;

import co.pragma.model.usuario.Usuario;
import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UsuarioRepository userRepository;

    private final BusinessValidator<Usuario> registrationValidator;

    public Mono<Usuario> registerUser(Usuario user) {
        return registrationValidator.validate(user)
                .flatMap(userRepository::save);
    }
}