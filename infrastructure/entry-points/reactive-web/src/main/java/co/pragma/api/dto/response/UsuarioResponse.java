package co.pragma.api.dto.response;

import lombok.Builder;

@Builder
public record UsuarioResponse(
        String id,
        String nombres,
        String apellidos,
        String tipoDocumento,
        String numeroDocumento,
        String fechaNacimiento,
        String direccion,
        String telefono,
        String email,
        String salarioBase,
        String rol
) {}
