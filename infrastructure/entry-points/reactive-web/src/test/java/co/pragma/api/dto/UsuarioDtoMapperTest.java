package co.pragma.api.dto;

import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UsuarioDtoMapperTest {

    private UsuarioDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UsuarioDtoMapper.class);
    }

    @Test
    void toModelShouldMapFieldsCorrectly() {
        RegistrarUsuarioDTO dto = RegistrarUsuarioDTO.builder()
                .nombres("Jhon")
                .apellidos("Doe")
                .tipoDocumento("CC")
                .numeroDocumento("123456789")
                .fechaNacimiento(String.valueOf(LocalDate.of(1990, 1, 1)))
                .direccion("Calle 123")
                .telefono("1234567890")
                .email("jhondoe@example.com")
                .rol("ADMIN")
                .salarioBase(BigDecimal.valueOf(3200000))
                .password("password123")
                .build();

        Usuario usuario = mapper.toDomain(dto);

        assertThat(usuario).isNotNull();
        assertThat(usuario.getNombres()).isEqualTo(dto.getNombres());
        assertThat(usuario.getApellidos()).isEqualTo(dto.getApellidos());
        assertThat(usuario.getNumeroDocumento()).isEqualTo(dto.getNumeroDocumento());
        assertThat(usuario.getTipoDocumento()).isEqualTo(TipoDocumento.CC);
        assertThat(usuario.getRol()).isNotNull();
        assertThat(usuario.getRol().getNombre()).isEqualTo("ADMIN");
        assertThat(usuario.getPasswordHash()).isNull(); // ignored in mapping
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

    @Test
    void mapTipoDocumentoShouldReturnCorrectEnum() {
        assertThat(mapper.mapTipoDocumento("CC")).isEqualTo(TipoDocumento.CC);
        assertThat(mapper.mapTipoDocumento("CE")).isEqualTo(TipoDocumento.CE);
        assertThatThrownBy(() -> mapper.mapTipoDocumento("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de documento no válido");
    }

    @Test
    void mapRolShouldReturnCorrectRol() {
        Rol rol = mapper.mapRol("ADMIN");
        assertThat(rol).isNotNull();
        assertThat(rol.getId()).isEqualTo(RolEnum.ADMIN.getId());
        assertThat(rol.getNombre()).isEqualTo("ADMIN");
        assertThat(rol.getDescripcion()).isEqualTo(RolEnum.ADMIN.getDescripcion());
    }
}