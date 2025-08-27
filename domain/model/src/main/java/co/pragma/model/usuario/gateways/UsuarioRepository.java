package co.pragma.model.usuario.gateways;

import co.pragma.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> save(Usuario user);
    Mono<Usuario> findByEmail(String email);
}
