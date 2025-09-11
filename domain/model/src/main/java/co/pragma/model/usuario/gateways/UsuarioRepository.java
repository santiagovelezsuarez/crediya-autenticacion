package co.pragma.model.usuario.gateways;

import co.pragma.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario user);
    Mono<Usuario> findByEmail(String email);
    Mono<Usuario> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
    Flux<Usuario> findByIdIn(List<UUID> userIds);
}
