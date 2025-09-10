package co.pragma.model.usuario;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.rol.Permission;
import co.pragma.model.usuario.gateways.SessionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionValidatorTest {

    @Mock
    private SessionProvider sessionProvider;

    @InjectMocks
    private PermissionValidator permissionValidator;

    private Session authorizedSession;
    private Session unauthorizedSession;

    @BeforeEach
    void setUp() {
        authorizedSession = Session.builder()
                .permissions(Set.of(Permission.REGISTRAR_USUARIO.name()))
                .build();
        unauthorizedSession = Session.builder()
                .permissions(Collections.emptySet())
                .build();
    }

    @Test
    void shouldSucceedWhenSessionHasPermission() {
        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(authorizedSession));

        StepVerifier.create(permissionValidator.requirePermission(Permission.REGISTRAR_USUARIO))
                .verifyComplete();
    }

    @Test
    void shouldThrowForbiddenExceptionWhenEmptyPermission() {
        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(unauthorizedSession));

        StepVerifier.create(permissionValidator.requirePermission(Permission.REGISTRAR_USUARIO))
                .verifyError(ForbiddenException.class);
    }

    @Test
    void shouldThrowForbiddenExceptionWhenSessionProviderReturnsEmpty() {
        when(sessionProvider.getCurrentSession()).thenReturn(Mono.empty());

        StepVerifier.create(permissionValidator.requirePermission(Permission.REGISTRAR_USUARIO))
                .verifyError(ForbiddenException.class);
    }
}
