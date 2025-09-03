package co.pragma.model.usuario;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioTest {

    @Test
    void builderCreatesCorrectUsuario() {
        Usuario usuario = Usuario.builder()
                .id(UUID.fromString("33ac0a47-79bd-4d78-8d08-c9c707cfa529"))
                .nombres("Santiago")
                .apellidos("Velez")
                .email("santi@example.com")
                .salarioBase(new BigDecimal("12750000"))
                .fechaNacimiento(LocalDate.of(1957, 1, 1))
                .build();

        assertEquals(UUID.fromString("33ac0a47-79bd-4d78-8d08-c9c707cfa529"), usuario.getId());
        assertEquals("Santiago", usuario.getNombres());
        assertEquals("Velez", usuario.getApellidos());
        assertEquals("santi@example.com", usuario.getEmail());
        assertEquals(new BigDecimal("12750000"), usuario.getSalarioBase());
        assertEquals(LocalDate.of(1957, 1, 1), usuario.getFechaNacimiento());
    }

}
