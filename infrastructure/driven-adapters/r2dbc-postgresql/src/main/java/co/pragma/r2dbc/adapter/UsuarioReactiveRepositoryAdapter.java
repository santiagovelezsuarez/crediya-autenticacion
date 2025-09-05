package co.pragma.r2dbc.adapter;

import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.r2dbc.repository.UsuarioReactiveRepository;
import co.pragma.r2dbc.entity.UsuarioEntity;
import co.pragma.r2dbc.mapper.UsuarioEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioReactiveRepositoryAdapter implements UsuarioRepository {

    private final UsuarioReactiveRepository usuarioRepository;
    private final RolReactiveRepositoryAdapter rolRepository;
    private final UsuarioEntityMapper mapper;

    @Override
    public Mono<Usuario> save(Usuario usuario) {
        log.debug("Registrando usuario: {}", usuario);
        return usuarioRepository.save(mapper.toEntity(usuario))
                .flatMap(this::mapToUsuario)
                .doOnSuccess(u -> log.trace("Usuario registrado con éxito: {}", u))
                .doOnError(e -> log.error("Error al registrar usuario: {}", e.getMessage()));
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .flatMap(this::mapToUsuario)
                .doOnNext(u -> log.trace("Usuario encontrado: {}", u))
                .doOnError(e -> log.error("Error al buscar usuario con email {} : {}", email, e.getMessage()));
    }

    @Override
    public Mono<Usuario> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento) {
        log.debug("Buscando usuario por tipo y número de documento: {} {}", tipoDocumento, numeroDocumento);
        return usuarioRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .flatMap(this::mapToUsuario)
                .doOnNext(u -> log.trace("Usuario encontrado: {}", u))
                .doOnError(e -> log.error("Error al buscar usuario con tipoDocumento {} y numeroDocumento {} : {}", tipoDocumento, numeroDocumento, e.getMessage()));
    }

    private Mono<Usuario> mapToUsuario(UsuarioEntity entity) {
        return rolRepository.findById(entity.getIdRol())
                .map(rol -> mapper.toDomain(entity, rol))
                .switchIfEmpty(Mono.just(mapper.toDomain(entity, null)));
    }
}
