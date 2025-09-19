package co.pragma.api.handler;

import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.mapper.UsuarioDtoMapper;
import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.*;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
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

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final DtoValidator dtoValidator;
    private final ResponseService responseService;
    private final UsuarioDtoMapper usuarioDtoMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        log.debug("Petición recibida para registrar usuario");
        return serverRequest.bodyToMono(RegistrarUsuarioDTO.class)
                .flatMap(dtoValidator::validate)
                .map(usuarioDtoMapper::toCommand)
                .flatMap(registrarUsuarioUseCase::execute)
                .doOnNext(user -> log.trace("Usuario registrado con éxito: {}", user.getEmail()))
                .map(usuarioDtoMapper::toResponse)
                .flatMap(responseService::createdJson);
    }
}
