package co.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {
    @Id
    @Column("id_usuario")
    private UUID id;
    private String nombres;
    private String apellidos;
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String passwordHash;
    private BigDecimal salarioBase;
    private Integer idRol;
}
