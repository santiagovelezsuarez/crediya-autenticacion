package co.pragma.api.security;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.security.Permission;
import co.pragma.model.security.Role;
import co.pragma.model.usuario.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SessionValidator {

    public Mono<Void> validatePermission(Session session, Permission permission) {
        log.debug("Validating permission {}", permission);
        return Mono.justOrEmpty(session.getRole())
                .map(Role::valueOf)
                .filter(rol -> rol.hasPermission(permission))
                .switchIfEmpty(Mono.error(new ForbiddenException("No tiene permisos para esta acción")))
                .then();
    }
}
