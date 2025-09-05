package co.pragma.model.usuario.gateways;

import co.pragma.model.usuario.Session;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<Session> authenticate(String email, String password);
}

