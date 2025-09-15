package co.pragma.r2dbc.mapper;

import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {

    public Usuario toDomain(UsuarioEntity usuarioEntity) {
        return Usuario.builder()
                .id(usuarioEntity.getId())
                .nombres(usuarioEntity.getNombres())
                .apellidos(usuarioEntity.getApellidos())
                .tipoDocumento(TipoDocumento.valueOf(usuarioEntity.getTipoDocumento()))
                .numeroDocumento(usuarioEntity.getNumeroDocumento())
                .fechaNacimiento(usuarioEntity.getFechaNacimiento())
                .direccion(usuarioEntity.getDireccion())
                .telefono(usuarioEntity.getTelefono())
                .email(usuarioEntity.getEmail())
                .passwordHash(usuarioEntity.getPasswordHash())
                .salarioBase(usuarioEntity.getSalarioBase())
                .build();
    }

    public Usuario toDomainWithRole(UsuarioEntity usuarioEntity, Rol rol) {
        return Usuario.builder()
                .id(usuarioEntity.getId())
                .nombres(usuarioEntity.getNombres())
                .apellidos(usuarioEntity.getApellidos())
                .tipoDocumento(TipoDocumento.fromCodigo(usuarioEntity.getTipoDocumento()))
                .numeroDocumento(usuarioEntity.getNumeroDocumento())
                .fechaNacimiento(usuarioEntity.getFechaNacimiento())
                .direccion(usuarioEntity.getDireccion())
                .telefono(usuarioEntity.getTelefono())
                .email(usuarioEntity.getEmail())
                .passwordHash(usuarioEntity.getPasswordHash())
                .salarioBase(usuarioEntity.getSalarioBase())
                .rol(rol)
                .build();
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
}