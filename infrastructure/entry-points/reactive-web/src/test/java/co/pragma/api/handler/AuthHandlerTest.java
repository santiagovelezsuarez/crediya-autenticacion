package co.pragma.api.handler;

import co.pragma.api.dto.AutenticarUsuarioDTO;
import co.pragma.api.dto.LoginResponseDTO;
import co.pragma.api.handler.service.ResponseService;
import co.pragma.api.handler.service.UsuarioService;
import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ResponseService responseService;

    @InjectMocks
    private AuthHandler authHandler;

    private AutenticarUsuarioDTO authDto;
    private Usuario usuario;
    private String testToken;

    @BeforeEach
    void setUp() {
        authDto = AutenticarUsuarioDTO.builder()
                .email("test@mail.com")
                .password("password123")
                .build();

        usuario = Usuario.builder()
                .id(UUID.fromString("1d2c9431-7e83-4a6f-b4c6-2c9c7f68c342"))
                .email("test@mail.com")
                .rol(new Rol(1, "ADMIN", "Administrador"))
                .build();

        testToken = "valid.jwt.token";
    }

    @Test
    void shouldAuthenticateSuccessfullyAndReturnToken() {
        ServerRequest mockRequest = Mockito.mock(ServerRequest.class);
        when(mockRequest.bodyToMono(AutenticarUsuarioDTO.class)).thenReturn(Mono.just(authDto));

        UsuarioService.AuthResult authResult = new UsuarioService.AuthResult(usuario, testToken);
        when(usuarioService.authenticate(any(AutenticarUsuarioDTO.class))).thenReturn(Mono.just(authResult));

        Mono<ServerResponse> mockResponse = ServerResponse.ok().build();
        when(responseService.okJson(any(LoginResponseDTO.class))).thenReturn(mockResponse);

        Mono<ServerResponse> responseMono = authHandler.listenAuthenticate(mockRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                    return true;
                })
                .verifyComplete();

        verify(usuarioService).authenticate(authDto);
        verify(responseService).okJson(any(LoginResponseDTO.class));
    }
}

