package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.AutenticarUsuarioDTO;
import co.pragma.api.dto.response.LoginResponseDTO;
import co.pragma.api.security.JwtService;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.AutenticarUsuarioUseCase;
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

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final DtoValidator dtoValidator;
    private final JwtService jwtService;
    private final ResponseService responseService;

    public Mono<ServerResponse> listenAuthenticate(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(AutenticarUsuarioDTO.class)
                .flatMap(dtoValidator::validate)
                .flatMap(dto -> autenticarUsuarioUseCase.execute(dto.getEmail(), dto.getPassword()))
                .map(usuario -> toLoginResponseDTO(usuario, jwtService.generateToken(usuario)))
                .flatMap(responseService::okJson);
    }

    private LoginResponseDTO toLoginResponseDTO(Usuario usuario, String token) {
        return LoginResponseDTO.builder()
                .userId(String.valueOf(usuario.getId()))
                .email(usuario.getEmail())
                .role(usuario.getRol().getNombre())
                .token(token)
                .build();
    }
}
