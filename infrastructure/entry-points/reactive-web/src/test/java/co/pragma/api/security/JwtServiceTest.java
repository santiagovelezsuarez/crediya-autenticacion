package co.pragma.api.security;

import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("supersecretkeysupersecretkeysupersecretkey", 3600L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("test@mail.com");
        usuario.setRol(Rol.builder().nombre("ADMIN").build());

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo(usuario.getEmail());
        assertThat(jwtService.getRoleFromToken(token)).isEqualTo(usuario.getRol().getNombre());
    }

    @Test
    void shouldGenerateTokenWithValidClaims() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("test@mail.com");
        usuario.setRol(Rol.builder().nombre("ADMIN").build());

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);

        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("test@mail.com");
        assertThat(jwtService.getRoleFromToken(token)).isEqualTo("ADMIN");
    }

    @Test
    void shouldValidateNonExpiredToken() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("test@mail.com");
        usuario.setRol(Rol.builder().nombre("ADMIN").build());

        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.validateToken(token)).isTrue();
    }
}
