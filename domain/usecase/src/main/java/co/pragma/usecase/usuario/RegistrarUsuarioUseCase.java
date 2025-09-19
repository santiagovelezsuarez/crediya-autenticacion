package co.pragma.usecase.usuario;

import co.pragma.exception.business.RolNotFoundException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.command.RegistrarUsuarioCommand;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository userRepository;
    private final RolRepository rolRepository;
    private final SalarioRangeValidator salarioRangeValidator;
    private final UniqueEmailValidator uniqueEmailValidator;
    private final UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidadValidator;
    private final PasswordEncoderService passwordEncoder;

    public Mono<Usuario> execute(RegistrarUsuarioCommand cmd) {
        return validateBusinessRules(cmd)
                .then(hashPassword(cmd))
                .flatMap(this::resolveRole)
                .flatMap(userRepository::save);
    }

    private Mono<Usuario> hashPassword(RegistrarUsuarioCommand cmd) {
        return passwordEncoder.encodeReactive(cmd.rawPassword())
                .map(hash -> Usuario.builder()
                        .nombres(cmd.nombres())
                        .apellidos(cmd.apellidos())
                        .tipoDocumento(TipoDocumento.fromCodigo(cmd.tipoDocumento()))
                        .numeroDocumento(cmd.numeroDocumento())
                        .fechaNacimiento(cmd.fechaNacimiento())
                        .direccion(cmd.direccion())
                        .telefono(cmd.telefono())
                        .email(cmd.email())
                        .passwordHash(hash)
                        .salarioBase(cmd.salarioBase())
                        .rol(Rol.builder().nombre(cmd.rol()).build())
                        .build());
    }

    private Mono<Usuario> resolveRole(Usuario user) {
        return rolRepository.findByNombre(user.getRol().getNombre())
                .switchIfEmpty(Mono.error(new RolNotFoundException()))
                .map(rol -> user.toBuilder().rol(rol).build());
    }

    private Mono<Void> validateBusinessRules(RegistrarUsuarioCommand cmd) {
        Mono<Void> salarioValidation = salarioRangeValidator.validate(cmd.salarioBase());
        Mono<Void> emailValidation = uniqueEmailValidator.validate(cmd.email());
        Mono<Void> documentoValidation = uniqueDocumentoIdentidadValidator.validate(cmd.tipoDocumento(), cmd.numeroDocumento());

        return Mono.when(salarioValidation, emailValidation, documentoValidation);
    }
}