package co.pragma.model.usuario;

import co.pragma.model.rol.Rol;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Usuario {
    private UUID id;
    private String nombres;
    private String apellidos;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String passwordHash;
    private BigDecimal salarioBase;
    private Rol rol;
}

