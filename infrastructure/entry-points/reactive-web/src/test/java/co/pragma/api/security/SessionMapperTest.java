package co.pragma.api.security;

import co.pragma.model.rol.Permission;
import co.pragma.model.usuario.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    @Mock
    private Authentication auth;

    @Test
    void shouldDefaultRoleWhenNoAuthorities() {
        Session session = SessionMapper.toSession(auth);
        assertThat(session.getRole()).isEqualTo("PUBLIC");
    }

    @Test
    void shouldReturnDefaultWhenAuthIsNull() {
        Session session = SessionMapper.toSession(null);

        assertThat(session.getUserId()).isEqualTo("");
        assertThat(session.getRole()).isEqualTo("PUBLIC");
    }

    @Test
    void shouldCreateSessionForAuthenticatedUser() {
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority(Permission.REGISTRAR_USUARIO.name()),
                new SimpleGrantedAuthority(Permission.APROBAR_SOLICITUD.name())
        );

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("test_user_id");
        doReturn(authorities).when(auth).getAuthorities();

        Session session = SessionMapper.toSession(auth);

        assertThat(session.getUserId()).isEqualTo("test_user_id");
        assertThat(session.getRole()).isEqualTo("ADMIN");
        assertThat(session.getPermissions()).containsExactlyInAnyOrder(Permission.REGISTRAR_USUARIO.name(), Permission.APROBAR_SOLICITUD.name());
    }
}
