package co.pragma.model.usuario;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.rol.Permission;
import co.pragma.model.usuario.gateways.SessionProvider;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PermissionValidator {

    private final SessionProvider sessionProvider;

    public Mono<Void> requirePermission(Permission permission) {
        return sessionProvider.getCurrentSession()
                .filter(session -> session.hasPermission(permission))
                .switchIfEmpty(Mono.error(new ForbiddenException()))
                .then();
    }
}
