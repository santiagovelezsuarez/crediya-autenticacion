package co.pragma.model.rol.gateways;

import co.pragma.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Rol> findById(Integer id);

    Mono<Rol> findByNombre(String name);
}
