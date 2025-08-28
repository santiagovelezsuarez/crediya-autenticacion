package co.pragma.api;

import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.UsuarioRequest;
import co.pragma.base.exception.BusinessException;
import co.pragma.usecase.usuario.UsuarioUseCase;
import common.api.dto.ErrorResponse;
import common.api.dto.ValidationUtil;
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

    private final UsuarioUseCase usuarioUseCase;

    private final UsuarioDtoMapper usuarioDtoMapper;

    private final Validator validator;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        log.info("Petición recibida para registrar usuario");
        return serverRequest
                .bodyToMono(UsuarioRequest.class)
                .doOnNext(req -> log.info("Request Body: {}", req))
                .flatMap(dto -> ValidationUtil.validate(dto, validator))
                .map(usuarioDtoMapper::toModel)
                .flatMap(usuarioUseCase::registerUser)
                .doOnSuccess(resp -> log.info("Usuario registrado exitosamente: {}", resp.getEmail()))
                .doOnError(err -> log.error("Error al registrar usuario: {}", err.getMessage()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(savedUser -> ServerResponse
                        .status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    public Mono<ServerResponse> listenFindByDocumento(ServerRequest serverRequest) {
        String numeroDocumento = serverRequest.pathVariable("numeroDocumento");
        String tipoDocumento = serverRequest.pathVariable("tipoDocumento");

        log.info("Petición recibida para buscar usuario por documento: {} {}", tipoDocumento, numeroDocumento);

        return usuarioUseCase.findByDocumento(numeroDocumento, tipoDocumento)
                .doOnNext(user -> log.info("Usuario encontrado: {}", user.getEmail()))
                .doOnError(err -> log.error("Error al buscar usuario por documento {} {}: {}",
                        tipoDocumento, numeroDocumento, err.getMessage()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(userResponse -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userResponse))
                .switchIfEmpty(
                        Mono.error(new BusinessException(
                                String.format("No se encontró usuario con documento %s %s", tipoDocumento, numeroDocumento)
                        ))
                );
    }
}
