package co.pragma.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El campo nombres no puede estar vacío")
    private String nombres;

    @NotBlank(message = "El campo apellidos no puede estar vacío")
    private String apellidos;

    private String fechaNacimiento;

    private String direccion;

    private String telefono;

    @NotBlank(message = "El campo email no puede estar vacío")
    @Email(message = "El campo email debe ser una dirección de correo electrónico válida")
    private String email;

    @Positive(message = "El salario debe ser positivo")
    private BigDecimal salarioBase;
}