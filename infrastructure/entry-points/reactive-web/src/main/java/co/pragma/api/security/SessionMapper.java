package co.pragma.api.security;

import co.pragma.model.usuario.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

public class SessionMapper {

    public static Mono<Session> fromSecurityContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> toSession(ctx.getAuthentication()));
    }

    public static Session toSession(Authentication auth) {
        if (auth == null) {
            return Session.builder()
                    .userId("_")
                    .role("_")
                    .build();
        }

        String userId = (String) auth.getPrincipal();

        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(granted -> granted.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        return Session.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
