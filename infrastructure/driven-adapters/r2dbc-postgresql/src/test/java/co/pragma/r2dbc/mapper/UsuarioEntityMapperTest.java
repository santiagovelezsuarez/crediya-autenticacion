package co.pragma.r2dbc.mapper;

import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.r2dbc.entity.UsuarioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioEntityMapperTest {

    private UsuarioEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UsuarioEntityMapper();
    }

    @Test
    void toDomainWithRoleShouldMapAllFields() {
        UUID id = UUID.randomUUID();
        Rol rol = new Rol(1, "ADMIN", "Administrador");

        UsuarioEntity entity = UsuarioEntity.builder()
                .id(id)
                .nombres("John")
                .apellidos("Doe")
                .tipoDocumento("CC")
                .numeroDocumento("123456")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("Street 123")
                .telefono("555-1234")
                .email("john.doe@mail.com")
                .passwordHash("hashedPass")
                .salarioBase(BigDecimal.valueOf(5000))
                .build();

        Usuario usuario = mapper.toDomainWithRole(entity, rol);

        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNombres()).isEqualTo("John");
        assertThat(usuario.getApellidos()).isEqualTo("Doe");
        assertThat(usuario.getTipoDocumento()).isEqualTo(TipoDocumento.CC);
        assertThat(usuario.getNumeroDocumento()).isEqualTo("123456");
        assertThat(usuario.getFechaNacimiento()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(usuario.getDireccion()).isEqualTo("Street 123");
        assertThat(usuario.getTelefono()).isEqualTo("555-1234");
        assertThat(usuario.getEmail()).isEqualTo("john.doe@mail.com");
        assertThat(usuario.getPasswordHash()).isEqualTo("hashedPass");
        assertThat(usuario.getSalarioBase()).isEqualByComparingTo(BigDecimal.valueOf(5000));
        assertThat(usuario.getRol()).isEqualTo(rol);
    }

    @Test
    void toDomainShouldMapAllFields() {
        UUID id = UUID.randomUUID();

        UsuarioEntity entity = UsuarioEntity.builder()
                .id(id)
                .nombres("John")
                .apellidos("Doe")
                .tipoDocumento("CC")
                .numeroDocumento("123456")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("Street 123")
                .telefono("555-1234")
                .email("john.doe@mail.com")
                .salarioBase(BigDecimal.valueOf(5000))
                .build();

        Usuario usuario = mapper.toDomain(entity);

        assertThat(usuario).isNotNull();
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getNombres()).isEqualTo("John");
        assertThat(usuario.getApellidos()).isEqualTo("Doe");
        assertThat(usuario.getTipoDocumento()).isEqualTo(TipoDocumento.CC);
        assertThat(usuario.getNumeroDocumento()).isEqualTo("123456");
        assertThat(usuario.getFechaNacimiento()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(usuario.getDireccion()).isEqualTo("Street 123");
        assertThat(usuario.getTelefono()).isEqualTo("555-1234");
        assertThat(usuario.getEmail()).isEqualTo("john.doe@mail.com");
        assertThat(usuario.getSalarioBase()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }

    @Test
    void toEntityShouldMapAllFields() {
        UUID id = UUID.randomUUID();
        Rol rol = new Rol(1, "ADMIN", "Administrador");

        Usuario usuario = Usuario.builder()
                .id(id)
                .nombres("Jane")
                .apellidos("Smith")
                .tipoDocumento(TipoDocumento.PA)
                .numeroDocumento("ABC123")
                .fechaNacimiento(LocalDate.of(1985, 5, 15))
                .direccion("Avenue 45")
                .telefono("999-8888")
                .email("jane.smith@mail.com")
                .passwordHash("passHash")
                .salarioBase(BigDecimal.valueOf(7000))
                .rol(rol)
                .build();

        UsuarioEntity entity = mapper.toEntity(usuario);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNombres()).isEqualTo("Jane");
        assertThat(entity.getApellidos()).isEqualTo("Smith");
        assertThat(entity.getTipoDocumento()).isEqualTo("PA");
        assertThat(entity.getNumeroDocumento()).isEqualTo("ABC123");
        assertThat(entity.getFechaNacimiento()).isEqualTo(LocalDate.of(1985, 5, 15));
        assertThat(entity.getDireccion()).isEqualTo("Avenue 45");
        assertThat(entity.getTelefono()).isEqualTo("999-8888");
        assertThat(entity.getEmail()).isEqualTo("jane.smith@mail.com");
        assertThat(entity.getPasswordHash()).isEqualTo("passHash");
        assertThat(entity.getSalarioBase()).isEqualByComparingTo(BigDecimal.valueOf(7000));
        assertThat(entity.getIdRol()).isEqualTo(1);
    }

    @Test
    void toEntityShouldHandleNullRol() {
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombres("NoRol")
                .apellidos("User")
                .tipoDocumento(TipoDocumento.TI)
                .numeroDocumento("TI123")
                .rol(null)
                .build();

        UsuarioEntity entity = mapper.toEntity(usuario);

        assertThat(entity).isNotNull();
        assertThat(entity.getIdRol()).isNull();
    }

}
