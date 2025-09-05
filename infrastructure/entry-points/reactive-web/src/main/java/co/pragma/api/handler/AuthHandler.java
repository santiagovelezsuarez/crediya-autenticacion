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
public class AuthHandler {

    private final UsuarioService usuarioService;
    private final ResponseService responseService;

    public Mono<ServerResponse> listenAuthenticate(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AutenticarUsuarioDTO.class)
                .flatMap(usuarioService::authenticate)
                .map(this::toLoginResponseDTO)
                .flatMap(responseService::okJson);
    }

    private LoginResponseDTO toLoginResponseDTO(UsuarioService.AuthResult res) {
        return LoginResponseDTO.builder()
                .userId(String.valueOf(res.usuario().getId()))
                .email(res.usuario().getEmail())
                .role(res.usuario().getRol().getNombre())
                .token(res.token())
                .build();
    }
}
