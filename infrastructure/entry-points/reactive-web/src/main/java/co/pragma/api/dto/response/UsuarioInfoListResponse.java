package co.pragma.api.dto.response;

import java.util.List;

public record UsuarioInfoListResponse(
    List<UsuarioInfoDTO> data
) {}
