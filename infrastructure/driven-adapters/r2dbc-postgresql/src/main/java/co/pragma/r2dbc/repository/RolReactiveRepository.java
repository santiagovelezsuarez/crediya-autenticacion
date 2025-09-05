package co.pragma.r2dbc.repository;

import co.pragma.r2dbc.entity.RolEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, Integer>, ReactiveQueryByExampleExecutor<RolEntity> {
    Mono<RolEntity> findByNombre(String nombre);
}
