package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.RolNotFoundException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.gateways.RolRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RolResolver {

    private final RolRepository rolRepository;

    public Mono<Rol> resolve(String rolName) {
        return rolRepository.findByNombre(rolName)
                .switchIfEmpty(Mono.error(new RolNotFoundException("Rol no encontrado: " + rolName)));
    }
}
