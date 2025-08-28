package co.pragma.r2dbc;

import co.pragma.r2dbc.entity.UsuarioEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, String>, ReactiveQueryByExampleExecutor<UsuarioEntity> {

    Mono<UsuarioEntity> findByEmail(String email);

    @Query("""
        SELECT * FROM usuarios
        WHERE numero_documento = $1
        AND tipo_documento = $2
        """)
    Mono<UsuarioEntity> findByDocumento(String numeroDocumento, String tipoDocumento);
}
