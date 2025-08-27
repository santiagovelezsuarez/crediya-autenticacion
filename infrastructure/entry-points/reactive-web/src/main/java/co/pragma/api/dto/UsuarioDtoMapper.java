package co.pragma.api.dto;

import co.pragma.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {

    @Mapping(target = "id", ignore = true)
    Usuario toModel(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
