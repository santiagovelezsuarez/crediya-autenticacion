package co.pragma.api.handler;

import co.pragma.api.dto.*;
import co.pragma.api.handler.service.ResponseService;
import co.pragma.api.handler.service.UsuarioService;
import co.pragma.exception.UsuarioNotFoundException;
import co.pragma.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioHandler {

    private final UsuarioService usuarioService;
    private final UsuarioUseCase usuarioUseCase;
    private final ResponseService responseService;
    private final UsuarioDtoMapper usuarioDtoMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        log.info("Petición recibida para registrar usuario");
        return serverRequest.bodyToMono(RegistrarUsuarioDTO.class)
                .flatMap(usuarioService::registerUser)
                .doOnNext(user -> log.info("Usuario registrado con éxito: {}", user.getEmail()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(responseService::createdJson);
    }

    public Mono<ServerResponse> listenFindByDocumento(ServerRequest serverRequest) {
        String numeroDocumento = serverRequest.pathVariable("numeroDocumento");
        String tipoDocumento = serverRequest.pathVariable("tipoDocumento");

        log.info("Petición recibida para buscar usuario por documento: {} {}", tipoDocumento, numeroDocumento);

        return usuarioUseCase.findByDocumento(numeroDocumento, tipoDocumento)
                .doOnNext(user -> log.info("Usuario encontrado: {}", user.getEmail()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(responseService::okJson)
                .switchIfEmpty(
                        Mono.error(new UsuarioNotFoundException("Usuario no encontrado con documento: " + tipoDocumento + " " + numeroDocumento))
                );
    }
}
