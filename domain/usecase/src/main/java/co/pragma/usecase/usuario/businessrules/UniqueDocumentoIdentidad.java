package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.DocumentoIdentidadAlreadyRegisteredException;
import co.pragma.gateways.BusinessValidator;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UniqueDocumentoIdentidad implements BusinessValidator<Usuario> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Mono<Usuario> validate(Usuario usuario) {


        return usuarioRepository.findByTipoDocumentoAndNumeroDocumento(usuario.getTipoDocumento(), usuario.getNumeroDocumento())
                .flatMap(found -> Mono.<Usuario>error(new DocumentoIdentidadAlreadyRegisteredException("El documento de identidad ya está registrado")))
                .switchIfEmpty(Mono.just(usuario));
    }
}
