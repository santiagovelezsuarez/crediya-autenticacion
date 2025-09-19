package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.api.mapper.UsuarioDtoMapper;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.command.RegistrarUsuarioCommand;
import co.pragma.usecase.security.PermissionValidator;
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
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Mock
    private ResponseService responseService;

    @Mock
    private UsuarioDtoMapper usuarioDtoMapper;

    @Mock
    private PermissionValidator permissionValidator;

    @Mock
    private DtoValidator dtoValidator;

    @InjectMocks
    private UsuarioHandler handler;

    @Test
    void shouldRegisterUser() {
        RegistrarUsuarioDTO requestDto = RegistrarUsuarioDTO.builder().email("test@mail.co").build();
        RegistrarUsuarioCommand cmd = RegistrarUsuarioCommand.builder().email("test@mail.co").build();
        Usuario usuario = Usuario.builder().email("test@mail.co").build();
        UsuarioResponse responseDto = UsuarioResponse.builder().email("test@mail.co").build();

        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(RegistrarUsuarioDTO.class)).thenReturn(Mono.just(requestDto));
        when(usuarioDtoMapper.toCommand(any(RegistrarUsuarioDTO.class))).thenReturn(cmd);
        when(registrarUsuarioUseCase.execute(cmd)).thenReturn(Mono.just(usuario));
        when(usuarioDtoMapper.toResponse(any(Usuario.class))).thenReturn(responseDto);
        when(responseService.createdJson(responseDto)).thenReturn(Mono.just(mock(ServerResponse.class)));
        when(dtoValidator.validate(any(RegistrarUsuarioDTO.class))).thenReturn(Mono.just(requestDto));

        Mono<ServerResponse> result = handler.listenRegisterUser(request);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(registrarUsuarioUseCase).execute(cmd);
        verify(usuarioDtoMapper).toResponse(usuario);
        verify(responseService).createdJson(responseDto);
    }
}
