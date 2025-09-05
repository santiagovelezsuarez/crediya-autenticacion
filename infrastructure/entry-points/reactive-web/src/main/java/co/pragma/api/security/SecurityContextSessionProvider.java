package co.pragma.api.security;

import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.gateways.SessionProvider;
import reactor.core.publisher.Mono;

public class SecurityContextSessionProvider implements SessionProvider {

    @Override
    public Mono<Session> getCurrentSession() {
        return SessionMapper.fromSecurityContext();
    }
}
