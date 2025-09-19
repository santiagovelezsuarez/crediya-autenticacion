package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
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
                .doOnNext(u -> log.trace("Usuario registrado con éxito: {}", u.getEmail()))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .flatMap(this::mapToUsuario)
                .doOnNext(u -> log.trace("findByEmail - Usuario encontrado: {}", u.getEmail()))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<Usuario> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento) {
        log.debug("Buscando usuario por tipo y número de documento: {} {}", tipoDocumento, numeroDocumento);
        return usuarioRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .flatMap(this::mapToUsuario)
                .doOnNext(u -> log.trace("findByTipoDocumentoAndNumeroDocumento - Usuario encontrado: {}", u))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    private Mono<Usuario> mapToUsuario(UsuarioEntity entity) {
        return rolRepository.findById(entity.getIdRol())
                .map(rol -> mapper.toDomainWithRole(entity, rol))
                .switchIfEmpty(Mono.just(mapper.toDomainWithRole(entity, null)));
    }
}
