package co.pragma.api.security;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.session.Permission;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.session.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SessionValidator {

    public Mono<Void> validatePermission(Session session, Permission permission) {
        log.debug("Validating permission {}", permission);
        return Mono.justOrEmpty(session.getRole())
                .map(RolEnum::valueOf)
                .filter(rol -> rol.hasPermission(permission))
                .switchIfEmpty(Mono.error(new ForbiddenException()))
                .then();
    }
}
