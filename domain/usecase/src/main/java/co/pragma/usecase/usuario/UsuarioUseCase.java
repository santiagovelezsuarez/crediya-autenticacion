package co.pragma.usecase.usuario;

import co.pragma.exception.*;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.security.Role;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.RolResolver;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    private final RolResolver rolResolver;
    private final SalarioRangeValidator salarioRangeValidator;
    private final UniqueEmailValidator uniqueEmailValidator;
    private final UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidadValidator;

    public Mono<Usuario> registerUser(Usuario user, Session session) {
        if(!RolEnum.ADMIN.getNombre().equals(session.getRole()))
            return Mono.error(new ForbiddenException("No tiene permisos para registrar usuarios"));

        return Mono.justOrEmpty(session.getRole())
                .map(Role::valueOf)
                .filter(rol -> rol.hasPermission(co.pragma.model.security.Permission.REGISTRAR_USUARIO))
                .switchIfEmpty(Mono.error(new ForbiddenException("No tiene permisos para registrar usuarios")))
                .then(salarioRangeValidator.validate(user.getSalarioBase()))
                .then(uniqueEmailValidator.validate(user.getEmail()))
                .then(uniqueDocumentoIdentidadValidator.validate(user.getTipoDocumento().name(), user.getNumeroDocumento()))
                .then(rolResolver.resolve(user.getRol().getNombre()))
                .flatMap(rol -> {
                    var newUser = user.toBuilder().rol(rol).build();
                    return userRepository.save(newUser);
                })
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    return new InfrastructureException("Error interno al registrar usuario: " + e.getMessage());
                });
    }

    public Mono<Usuario> findByDocumento(String numeroDocumento, String tipoDocumento) {
        return userRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException("Usuario con documento " + tipoDocumento + " " + numeroDocumento + " no encontrado")))
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    return new InfrastructureException("Error interno al buscar usuario: " + e.getMessage());
                });
    }

    public Mono<Usuario> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationException("Usuario no encontrado")))
                .flatMap(usuario -> {
                    if (passwordEncoderService.matches(password, usuario.getPasswordHash())) {
                        return Mono.just(usuario);
                    }
                    return Mono.error(new AuthenticationException("Credenciales inválidas"));
                })
                .onErrorMap(e -> {
                    if (e instanceof BusinessException) {
                        return e;
                    }
                    return new InfrastructureException("Error interno al autenticar usuario: " + e.getMessage());
                });
    }
}