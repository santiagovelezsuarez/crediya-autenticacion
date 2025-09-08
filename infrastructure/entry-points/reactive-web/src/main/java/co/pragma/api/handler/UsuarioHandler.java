package co.pragma.api.handler;

import co.pragma.api.dto.*;
import co.pragma.api.handler.service.ResponseService;
import co.pragma.api.handler.service.UsuarioService;
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
    private final ResponseService responseService;
    private final UsuarioDtoMapper usuarioDtoMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        log.debug("Petición recibida para registrar usuario");
        return serverRequest.bodyToMono(RegistrarUsuarioDTO.class)
                .flatMap(usuarioService::registerUser)
                .doOnNext(user -> log.trace("Usuario registrado con éxito: {}", user.getEmail()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(responseService::createdJson);
    }
}
