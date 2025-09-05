package co.pragma.api.dto;

import co.pragma.model.rol.Rol;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoDocumento", expression = "java(mapTipoDocumento(dto.getTipoDocumento()))")
    @Mapping(target = "rol", expression = "java(mapRol(dto.getRol()))")
    @Mapping(target = "passwordHash", ignore = true)
    Usuario toModel(RegistrarUsuarioDTO dto);

    @Mapping(target = "rol", expression = "java(usuario.getRol() != null ? usuario.getRol().getNombre() : null)")
    UsuarioResponse toResponse(Usuario usuario);

    default TipoDocumento mapTipoDocumento(String codigo) {
        return TipoDocumento.fromCodigo(codigo);
    }

    default Rol mapRol(String nombreRol) {
        RolEnum rolEnum = RolEnum.fromNombre(nombreRol);
        return Rol.builder()
                .id(rolEnum.getId())
                .nombre(rolEnum.getNombre())
                .descripcion(rolEnum.getDescripcion())
                .build();
    }
}
