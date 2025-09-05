package co.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutenticarUsuarioDTO {
    @NotBlank(message = "El campo 'email' no debe estar vacío")
    @Email(message = "El campo 'email' debe tener un formato válido")
    private String email;

    @NotBlank(message = "El campo 'password' no debe estar vacío")
    private String password;
}
