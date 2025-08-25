package co.pragma.api.dto;

import co.pragma.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {

    Usuario toModel(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
