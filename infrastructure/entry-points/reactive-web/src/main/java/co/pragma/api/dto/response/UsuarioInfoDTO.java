package co.pragma.api.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UsuarioInfoDTO(
        String id,
        String email,
        String nombre,
        BigDecimal salarioBase
) {}