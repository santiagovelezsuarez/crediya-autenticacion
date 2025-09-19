package co.pragma.usecase.usuario;

import co.pragma.exception.business.AuthenticationException;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public Mono<Usuario> execute(String email, String password) {
        return userRepository
                .findByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationException()))
                .flatMap(usuario -> validatePassword(usuario, password));
    }

    private Mono<Usuario> validatePassword(Usuario usuario, String rawPassword) {
        if (passwordEncoderService.matches(rawPassword, usuario.getPasswordHash()))
            return Mono.just(usuario);

        return Mono.error(new AuthenticationException());
    }
}
