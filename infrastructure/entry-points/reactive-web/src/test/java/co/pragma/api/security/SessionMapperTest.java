package co.pragma.api.security;

import co.pragma.model.usuario.Session;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class SessionMapperTest {

    @Test
    void shouldDefaultRoleWhenNoAuthorities() {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("123");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());

        Session session = SessionMapper.toSession(auth);

        assertThat(session.getRole()).isEqualTo("USER");
    }

    @Test
    void shouldReturnDefaultWhenAuthIsNull() {
        Session session = SessionMapper.toSession(null);

        assertThat(session.getUserId()).isEqualTo("_");
        assertThat(session.getRole()).isEqualTo("_");
    }

}
