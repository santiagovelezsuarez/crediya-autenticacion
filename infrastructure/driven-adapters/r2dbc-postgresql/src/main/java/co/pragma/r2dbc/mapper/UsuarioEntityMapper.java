package co.pragma.r2dbc.mapper;

import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        return baseDomain(entity).build();
    }

    public Usuario toDomainWithRole(UsuarioEntity entity, Rol rol) {
        return baseDomain(entity).rol(rol).build();
    }

    public UsuarioEntity toEntity(Usuario usuario) {
        return UsuarioEntity.builder()
                .id(usuario.getId())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .tipoDocumento(usuario.getTipoDocumento().getCodigo())
                .numeroDocumento(usuario.getNumeroDocumento())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .direccion(usuario.getDireccion())
                .telefono(usuario.getTelefono())
                .email(usuario.getEmail())
                .passwordHash(usuario.getPasswordHash())
                .salarioBase(usuario.getSalarioBase())
                .idRol(usuario.getRol() != null ? usuario.getRol().getId() : null)
                .build();
    }

    private Usuario.UsuarioBuilder baseDomain(UsuarioEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .tipoDocumento(TipoDocumento.fromCodigo(entity.getTipoDocumento()))
                .numeroDocumento(entity.getNumeroDocumento())
                .fechaNacimiento(entity.getFechaNacimiento())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .salarioBase(entity.getSalarioBase());
    }

}