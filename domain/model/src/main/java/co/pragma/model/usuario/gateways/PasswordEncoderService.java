package co.pragma.model.usuario.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncoderService {
    boolean matches(CharSequence raw, String hash);

    Mono<String> encodeReactive(CharSequence raw);
}
