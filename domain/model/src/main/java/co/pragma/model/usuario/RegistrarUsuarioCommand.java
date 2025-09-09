package co.pragma.model.usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistrarUsuarioCommand(
        String nombres,
        String apellidos,
        String tipoDocumento,
        String numeroDocumento,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String email,
        String rawPassword,
        BigDecimal salarioBase,
        String rol
) {}
