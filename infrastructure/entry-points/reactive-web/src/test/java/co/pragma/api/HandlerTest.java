package co.pragma.api;

import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.UsuarioResponse;
import co.pragma.exception.UsuarioNotFoundException;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.UsuarioUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class HandlerTest {
    private UsuarioUseCase usuarioUseCase;
    private UsuarioDtoMapper usuarioDtoMapper;
    private Validator validator;
    private Handler handler;

    @BeforeEach
    void setUp() {
        usuarioUseCase = Mockito.mock(UsuarioUseCase.class);
        usuarioDtoMapper = Mockito.mock(UsuarioDtoMapper.class);
        validator = Mockito.mock(Validator.class);
        handler = new Handler(usuarioUseCase, usuarioDtoMapper, validator);
    }

    @Test
    void shouldReturn201WhenUserIsCreated() {
        RegistrarUsuarioDTO request = new RegistrarUsuarioDTO();
        request.setEmail("test@example.com");

        Usuario usuarioModel = new Usuario();
        usuarioModel.setEmail("test@example.com");

        Usuario usuarioSaved = new Usuario();
        usuarioSaved.setEmail("test@example.com");

        UsuarioResponse response = UsuarioResponse.builder()
                .email("test@example.co")
                .build();

        when(usuarioDtoMapper.toModel(any())).thenReturn(usuarioModel);
        when(usuarioUseCase.registerUser(usuarioModel)).thenReturn(Mono.just(usuarioSaved));
        when(usuarioDtoMapper.toResponse(usuarioSaved)).thenReturn(response);

        ServerRequest serverRequest = MockServerRequest.builder()
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.listenRegisterUser(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().value() == 201
                )
                .verifyComplete();
    }

    @Test
    void shouldLogErrorWhenRegisterUserUseCaseFails() {
        RegistrarUsuarioDTO request = new RegistrarUsuarioDTO();
        request.setEmail("fail@example.com");

        Usuario usuarioModel = new Usuario();
        usuarioModel.setEmail("fail@example.com");

        when(usuarioDtoMapper.toModel(any())).thenReturn(usuarioModel);
        when(usuarioUseCase.registerUser(usuarioModel))
                .thenReturn(Mono.error(new RuntimeException("Error al conectar con la base de datos")));

        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = handler.listenRegisterUser(serverRequest);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldReturn200WhenUserExists() {
        Usuario usuario = new Usuario();
        usuario.setEmail("found@example.com");

        UsuarioResponse response = UsuarioResponse.builder()
                .email("found@example.co")
                .build();

        when(usuarioUseCase.findByDocumento("123", "CC")).thenReturn(Mono.just(usuario));
        when(usuarioDtoMapper.toResponse(usuario)).thenReturn(response);

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("numeroDocumento", "123")
                .pathVariable("tipoDocumento", "CC")
                .build();

        Mono<ServerResponse> result = handler.listenFindByDocumento(serverRequest);

        StepVerifier.create(result)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode().is2xxSuccessful()
                )
                .verifyComplete();
    }

    @Test
    void shouldErrorWhenUserNotFound() {
        when(usuarioUseCase.findByDocumento("123", "CC")).thenReturn(Mono.empty());

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("numeroDocumento", "123")
                .pathVariable("tipoDocumento", "CC")
                .build();

        Mono<ServerResponse> result = handler.listenFindByDocumento(serverRequest);

        StepVerifier.create(result)
                .expectError(UsuarioNotFoundException.class)
                .verify();
    }

    @Test
    void shouldLogErrorWhenFindByDocumentoUseCaseFails() {
        when(usuarioUseCase.findByDocumento("123", "CC"))
                .thenReturn(Mono.error(new RuntimeException("Error al conectar con la base de datos")));

        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("numeroDocumento", "123")
                .pathVariable("tipoDocumento", "CC")
                .build();

        Mono<ServerResponse> result = handler.listenFindByDocumento(serverRequest);

        StepVerifier.create(result)
                .expectError(RuntimeException.class) // el flujo se rompe por la excepción
                .verify();
    }
}
