package co.pragma.usecase.usuario;

import co.pragma.error.ErrorMessages;
import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.security.Role;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.RolResolver;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository userRepository;
    private final RolResolver rolResolver;
    private final SalarioRangeValidator salarioRangeValidator;
    private final UniqueEmailValidator uniqueEmailValidator;
    private final UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidadValidator;

    public Mono<Usuario> execute(Usuario user, Session session) {
        return validatePermission(session)
                .then(validateBusinessRules(user))
                .then(resolveRole(user))
                .flatMap(userRepository::save);
    }

    private Mono<Usuario> resolveRole(Usuario user) {
        return rolResolver.resolve(user.getRol().getNombre())
                .map(rol -> user.toBuilder().rol(rol).build());
    }

    private Mono<Void> validatePermission(Session session) {
        return Mono.justOrEmpty(session.getRole())
                .map(Role::valueOf)
                .filter(rol -> rol.hasPermission(co.pragma.model.security.Permission.REGISTRAR_USUARIO))
                .switchIfEmpty(Mono.error(new ForbiddenException(ErrorMessages.FORBIDDEN)))
                .then();
    }

    private Mono<Void> validateBusinessRules(Usuario user) {
        Mono<Void> salarioValidation = salarioRangeValidator.validate(user.getSalarioBase());
        Mono<Void> emailValidation = uniqueEmailValidator.validate(user.getEmail());
        Mono<Void> documentoValidation = uniqueDocumentoIdentidadValidator.validate(user.getTipoDocumento().name(), user.getNumeroDocumento());

        return Mono.when(salarioValidation, emailValidation, documentoValidation);
    }

}