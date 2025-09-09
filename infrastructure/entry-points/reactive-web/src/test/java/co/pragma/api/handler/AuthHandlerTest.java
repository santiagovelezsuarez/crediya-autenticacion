package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.AutenticarUsuarioDTO;
import co.pragma.api.dto.LoginResponseDTO;
import co.pragma.api.security.JwtService;
import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.AutenticarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Mock
    private JwtService jwtService;

    @Mock
    private ResponseService responseService;

    @InjectMocks
    private AuthHandler authHandler;

    private AutenticarUsuarioDTO authDto;
    private Usuario mockUser;

    @BeforeEach
    void setUp() {
        authDto = AutenticarUsuarioDTO.builder()
                .email("test@mail.co")
                .password("password123")
                .build();

        mockUser = Usuario.builder()
                .email("test@mail.co")
                .rol(Rol.builder().nombre("ADMIN").build())
                .build();
    }

    @Test
    void shouldReturnValidResponseWhenAuthenticationIsSuccessful() {
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(AutenticarUsuarioDTO.class)).thenReturn(Mono.just(authDto));
        when(autenticarUsuarioUseCase.execute(authDto.getEmail(), authDto.getPassword())).thenReturn(Mono.just(mockUser));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock_token");
        when(responseService.okJson(any(LoginResponseDTO.class))).thenReturn(Mono.just(mock(ServerResponse.class)));

        Mono<ServerResponse> result = authHandler.listenAuthenticate(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(autenticarUsuarioUseCase).execute(authDto.getEmail(), authDto.getPassword());
        verify(responseService).okJson(any(LoginResponseDTO.class));
    }
}
