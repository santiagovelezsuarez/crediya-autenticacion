package co.pragma.usecase.usuario;

import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ConsultarUsuariosBatchUseCase {

    private final UsuarioRepository usuarioRepository;

    public Flux<Usuario> execute(List<UUID> userIds) {
        return usuarioRepository.findByIdIn(userIds);
    }
}
