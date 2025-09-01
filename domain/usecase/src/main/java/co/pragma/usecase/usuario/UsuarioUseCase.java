package co.pragma.usecase.usuario;

import co.pragma.gateways.BusinessValidator;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository userRepository;
    private final SalarioRangeValidator salarioRangeValidator;
    private final UniqueEmailValidator uniqueEmailValidator;

    public Mono<Usuario> registerUser(Usuario user) {
        return salarioRangeValidator.validate(user)
                .flatMap(uniqueEmailValidator::validate)
                .flatMap(userRepository::save);
    }

    public Mono<Usuario> findByDocumento(String numeroDocumento, String tipoDocumento) {
        return userRepository.findByDocumento(numeroDocumento, tipoDocumento);
    }
}