package co.pragma.api.mapper;

import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.UsuarioInfoDTO;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.command.RegistrarUsuarioCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class UsuarioDtoMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(String.valueOf(usuario.getId()))
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .tipoDocumento(usuario.getTipoDocumento() != null ? usuario.getTipoDocumento().getCodigo() : null)
                .numeroDocumento(usuario.getNumeroDocumento())
                .fechaNacimiento(String.valueOf(usuario.getFechaNacimiento()))
                .direccion(usuario.getDireccion())
                .telefono(usuario.getTelefono())
                .email(usuario.getEmail())
                .salarioBase(usuario.getSalarioBase())
                .rol(usuario.getRol() != null ? usuario.getRol().getNombre() : null)
                .build();
    }

    public RegistrarUsuarioCommand toCommand(RegistrarUsuarioDTO dto) {
        return RegistrarUsuarioCommand.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .tipoDocumento(dto.getTipoDocumento())
                .numeroDocumento(dto.getNumeroDocumento())
                .fechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()))
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .rawPassword(dto.getPassword())
                .salarioBase(dto.getSalarioBase())
                .rol(dto.getRol())
                .build();
    }

    public UsuarioInfoDTO toUsuarioInfoDTO(Usuario usuario) {
        return UsuarioInfoDTO.builder()
                .id(String.valueOf(usuario.getId()))
                .nombre(usuario.getNombres() + " " + usuario.getApellidos())
                .email(usuario.getEmail())
                .salarioBase(usuario.getSalarioBase())
                .build();
    }

    public List<UsuarioInfoDTO> toUsuarioInfoDtoList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toUsuarioInfoDTO)
                .toList();
    }

}
