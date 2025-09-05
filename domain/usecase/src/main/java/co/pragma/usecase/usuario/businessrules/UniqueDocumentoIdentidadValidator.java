package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.DocumentoIdentidadAlreadyRegisteredException;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UniqueDocumentoIdentidadValidator {

    private final UsuarioRepository usuarioRepository;

    public Mono<Void> validate(String tipoDocumento, String numeroDocumento) {
        return usuarioRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .flatMap(u -> Mono.error(new DocumentoIdentidadAlreadyRegisteredException("El documento de identidad ya está en uso")))
                .switchIfEmpty(Mono.empty())
                .then();
    }
}
