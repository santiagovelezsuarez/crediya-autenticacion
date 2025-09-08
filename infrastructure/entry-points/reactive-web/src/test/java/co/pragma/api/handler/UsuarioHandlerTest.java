package co.pragma.api.handler;

import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.UsuarioResponse;
import co.pragma.api.handler.service.ResponseService;
import co.pragma.api.handler.service.UsuarioService;
import co.pragma.exception.business.UsuarioNotFoundException;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Test
    void testListenFindByDocumento_success() {
        String tipo = "CC";
        String numero = "12345";
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        UsuarioResponse responseDto = UsuarioResponse.builder().email("test@example.com").build();

        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("tipoDocumento")).thenReturn(tipo);
        when(request.pathVariable("numeroDocumento")).thenReturn(numero);

        when(registrarUsuarioUseCase.findByDocumento(numero, tipo)).thenReturn(Mono.just(usuario));
        when(usuarioDtoMapper.toResponse(usuario)).thenReturn(responseDto);
        when(responseService.okJson(responseDto)).thenReturn(Mono.just(mock(ServerResponse.class)));

        Mono<ServerResponse> result = handler.listenFindByDocumento(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(registrarUsuarioUseCase).findByDocumento(numero, tipo);
        verify(usuarioDtoMapper).toResponse(usuario);
        verify(responseService).okJson(responseDto);
    }

    @Test
    void testListenFindByDocumento_notFound() {
        String tipo = "CC";
        String numero = "99999";

        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("tipoDocumento")).thenReturn(tipo);
        when(request.pathVariable("numeroDocumento")).thenReturn(numero);

        when(registrarUsuarioUseCase.findByDocumento(numero, tipo)).thenReturn(Mono.empty());

        Mono<ServerResponse> result = handler.listenFindByDocumento(request);

        StepVerifier.create(result)
                .expectError(UsuarioNotFoundException.class)
                .verify();

        verify(registrarUsuarioUseCase).findByDocumento(numero, tipo);
        verifyNoInteractions(usuarioDtoMapper);
        verifyNoInteractions(responseService);
    }

}
