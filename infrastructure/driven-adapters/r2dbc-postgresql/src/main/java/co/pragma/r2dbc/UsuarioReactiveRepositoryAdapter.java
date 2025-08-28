package co.pragma.r2dbc;

import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.r2dbc.entity.UsuarioEntity;
import co.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        String,
        UsuarioReactiveRepository
> implements UsuarioRepository {


    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class));
    }

    @Override
    public Mono<Usuario> save(Usuario user) {
        log.info("Registrando usuario: {}", user);
        return  super.save(user)
                .doOnSuccess(u -> log.info("Usuario registrado con éxito: {}", u))
                .doOnError(e -> log.error("Error al registrar usuario: {}", e.getMessage()));
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        log.info("Buscando usuario: {}", email);
        return repository.findByEmail(email)
                .map(entity -> mapper.map(entity, Usuario.class))
                .doOnNext(u -> log.info("Usuario encontrado: {}", u))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Usuario no encontrado con email={}", email);
                    return Mono.empty();
                }))
                .doOnError(e -> log.error("Error al buscar usuario {} : {}", email, e.getMessage()));
    }


    @Override
    public Mono<Usuario> findByDocumento(String numeroDocumento, String tipoDocumento) {
        log.info("Buscando usuario: {}", numeroDocumento);
        return repository.findByDocumento(numeroDocumento, tipoDocumento)
                .map(entity -> mapper.map(entity, Usuario.class))
                .doOnNext(u -> log.info("Usuario encontrado: {}", u))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Usuario no encontrado con documento={}", numeroDocumento);
                    return Mono.empty();
                }))
                .doOnError(e -> log.error("Error al buscar usuario {} : {}", numeroDocumento, e.getMessage()));
    }
}
