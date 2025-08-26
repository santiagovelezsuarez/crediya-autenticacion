package co.pragma.api;

import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.UsuarioRequest;
import co.pragma.api.dto.ValidationUtil;
import co.pragma.usecase.usuario.UsuarioUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UsuarioUseCase registerUserUseCase;

    private final UsuarioDtoMapper usuarioDtoMapper;

    private final Validator validator;


    public Mono<ServerResponse> listenSaveTask(ServerRequest serverRequest) {
        log.info("Petición recibida para registrar usuario");
        return serverRequest.bodyToMono(UsuarioRequest.class)
                .doOnNext(req -> log.info("Request Body: {}", req))
                .flatMap(dto -> ValidationUtil.validate(dto, validator))
                .map(usuarioDtoMapper::toModel)
                .flatMap(registerUserUseCase::registerUser)
                .doOnSuccess(resp -> log.info("Usuario registrado exitosamente: {}", resp.getEmail()))
                .doOnError(err -> log.error("Error al registrar usuario: {}", err.getMessage()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

}
