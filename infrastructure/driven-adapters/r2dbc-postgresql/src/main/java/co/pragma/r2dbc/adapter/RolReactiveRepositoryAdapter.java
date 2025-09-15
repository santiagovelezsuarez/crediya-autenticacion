package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.r2dbc.repository.RolReactiveRepository;
import co.pragma.r2dbc.entity.RolEntity;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Integer,
        RolReactiveRepository
> implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper){
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Rol> findById(Integer id) {
        log.debug("Buscando rol por ID: {}", id);
        return repository.findById(id)
                .map(entity -> mapper.map(entity, Rol.class))
                .doOnNext(rol -> log.trace("findById - Rol encontrado: {}", rol))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }

    @Override
    public Mono<Rol> findByNombre(String nombre) {
        log.debug("Buscando rol por nombre: {}", nombre);
        return repository.findByNombre(nombre)
                .map(entity -> mapper.map(entity, Rol.class))
                .doOnNext(rol -> log.trace("Rol encontrado: {}", rol))
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));
    }
}
