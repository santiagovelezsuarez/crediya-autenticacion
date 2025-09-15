package co.pragma.api.mapper;

import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioDtoMapperTest {

    private UsuarioDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioDtoMapper();
    }

    @Test
    void toResponseShouldMapUsuarioToUsuarioResponse() {
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombres("Jhon")
                .apellidos("Doe")
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("123456789")
                .email("jhondoe@example.com")
                .rol(Rol.builder().nombre("ADMIN").build())
                .build();

        UsuarioResponse response = mapper.toResponse(usuario);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(usuario.getEmail());
        assertThat(response.id()).isEqualTo(usuario.getId().toString());
        assertThat(response.rol()).isEqualTo(usuario.getRol().getNombre());
    }
}
