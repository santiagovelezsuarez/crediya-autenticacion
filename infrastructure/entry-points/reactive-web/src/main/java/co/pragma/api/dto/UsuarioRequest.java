package co.pragma.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El campo nombres no puede estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no puede estar vacío")
    private String apellidos;

    @NotBlank(message = "El campo tipoDocumento no puede estar vacío")
    @Pattern(regexp = "CC|CE|PA|TI", message = "El tipo de documento no es válido. Debe ser CC, CE, PA o TI.")
    private String tipoDocumento;

    @NotBlank(message = "El campo numeroDocumento no puede estar vacío")
    private String numeroDocumento;

    private String fechaNacimiento;

    private String direccion;

    private String telefono;

    @NotBlank(message = "El campo email no puede estar vacío")
    @Email(message = "El campo email debe ser una dirección de correo electrónico válida")
    private String email;

    @Positive(message = "El salario debe ser positivo")
    private BigDecimal salarioBase;
}