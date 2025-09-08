package co.pragma.usecase.usuario;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.exception.business.RolNotFoundException;
import co.pragma.model.rol.Permission;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.Usuario;
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

    public Mono<Usuario> execute(Usuario user, Session session) {
        return validatePermission(session)
                .then(Mono.defer(() -> validateBusinessRules(user)))
                .then(Mono.defer(() -> resolveRole(user)))
                .flatMap(userRepository::save);
    }

    private Mono<Usuario> resolveRole(Usuario user) {
        return rolRepository.findByNombre(user.getRol().getNombre())
                .switchIfEmpty(Mono.error(new RolNotFoundException()))
                .map(rol -> user.toBuilder().rol(rol).build());
    }

    private Mono<Void> validatePermission(Session session) {
        return Mono.justOrEmpty(session.getRole())
                .map(RolEnum::valueOf)
                .filter(rol -> rol.hasPermission(Permission.REGISTRAR_USUARIO))
                .switchIfEmpty(Mono.error(new ForbiddenException()))
                .then();
    }

    private Mono<Void> validateBusinessRules(Usuario user) {
        Mono<Void> salarioValidation = salarioRangeValidator.validate(user.getSalarioBase());
        Mono<Void> emailValidation = uniqueEmailValidator.validate(user.getEmail());
        Mono<Void> documentoValidation = uniqueDocumentoIdentidadValidator.validate(user.getTipoDocumento().name(), user.getNumeroDocumento());

        return Mono.when(salarioValidation, emailValidation, documentoValidation);
    }

}