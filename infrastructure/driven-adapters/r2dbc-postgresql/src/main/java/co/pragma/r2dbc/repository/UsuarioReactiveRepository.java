package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, UUID>, ReactiveQueryByExampleExecutor<UsuarioEntity> {
    Mono<UsuarioEntity> findByEmail(String email);

    Mono<UsuarioEntity> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
}
