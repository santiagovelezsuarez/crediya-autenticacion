package co.pragma.r2dbc;

import co.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, String>, ReactiveQueryByExampleExecutor<UsuarioEntity> {

    Mono<UsuarioEntity> findByEmail(String email);


    Mono<UsuarioEntity> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
}
