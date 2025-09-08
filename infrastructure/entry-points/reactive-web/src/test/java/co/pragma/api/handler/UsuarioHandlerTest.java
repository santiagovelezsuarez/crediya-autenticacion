package co.pragma.api.handler;

import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.UsuarioResponse;
import co.pragma.api.handler.service.ResponseService;
import co.pragma.api.handler.service.UsuarioService;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioHandlerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Mock
    private ResponseService responseService;

    @Mock
    private UsuarioDtoMapper usuarioDtoMapper;

    @InjectMocks
    private UsuarioHandler handler;

    @Test
    void shouldRegisterUser() {
        RegistrarUsuarioDTO dto = new RegistrarUsuarioDTO();
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        UsuarioResponse responseDto = UsuarioResponse.builder().email("test@example.com").build();

        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(RegistrarUsuarioDTO.class)).thenReturn(Mono.just(dto));
        when(usuarioService.registerUser(dto)).thenReturn(Mono.just(usuario));
        when(usuarioDtoMapper.toResponse(usuario)).thenReturn(responseDto);
        when(responseService.createdJson(responseDto)).thenReturn(Mono.just(mock(ServerResponse.class)));

        Mono<ServerResponse> result = handler.listenRegisterUser(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(usuarioService).registerUser(dto);
        verify(usuarioDtoMapper).toResponse(usuario);
        verify(responseService).createdJson(responseDto);
    }
}
