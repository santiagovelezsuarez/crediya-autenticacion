package co.pragma.api.handler.service;

import co.pragma.api.dto.AutenticarUsuarioDTO;
import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.security.JwtService;
import co.pragma.api.security.SessionValidator;
import co.pragma.exception.business.AuthenticationException;
import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.rol.Permission;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.SessionProvider;
import co.pragma.usecase.usuario.AutenticarUsuarioUseCase;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    @Mock
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    @Mock
    private SessionValidator sessionValidator;
    @Mock
    private UsuarioDtoMapper usuarioDtoMapper;
    @Mock
    private Validator validator;
    @Mock
    private JwtService jwtService;
    @Mock
    private SessionProvider sessionProvider;
    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private RegistrarUsuarioDTO dto;
    private Session session;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .email("jhondoe@mail.com")
                .passwordHash("hashed-pass")
                .build();

        dto = RegistrarUsuarioDTO.builder()
                .email("jhondoe@mail.co")
                .password("secret")
                .nombres("Jhon")
                .apellidos("Doe")
                .build();

        session = mock(Session.class);
    }

    @Test
    void authenticateShouldReturnAuthResultWhenSuccessful() {
        AutenticarUsuarioDTO dto = new AutenticarUsuarioDTO("jhondoe@mail.com", "secret");

        when(autenticarUsuarioUseCase.execute(dto.getEmail(), dto.getPassword()))
                .thenReturn(Mono.just(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token");

        StepVerifier.create(usuarioService.authenticate(dto))
                .expectNextMatches(authResult ->
                        authResult.usuario().equals(usuario) &&
                                authResult.token().equals("jwt-token"))
                .verifyComplete();
    }

    @Test
    void authenticateShouldFailWhenInvalidCredentials() {
        AutenticarUsuarioDTO dto = new AutenticarUsuarioDTO("wrong@mail.com", "bad");

        when(autenticarUsuarioUseCase.execute(dto.getEmail(), dto.getPassword()))
                .thenReturn(Mono.error(new AuthenticationException()));

        StepVerifier.create(usuarioService.authenticate(dto))
                .expectError(AuthenticationException.class)
                .verify();
    }

    @Test
    void registerUserShouldSucceedWhenAllValidationsPass() {
       Usuario mappedUser = usuario.toBuilder().passwordHash(null).build();

        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(session));
        when(sessionValidator.validatePermission(session, Permission.REGISTRAR_USUARIO)).thenReturn(Mono.empty());
        when(usuarioDtoMapper.toModel(dto)).thenReturn(mappedUser);
        when(passwordEncoderService.encodeReactive(dto.getPassword())).thenReturn(Mono.just("hashed-pass"));
        when(registrarUsuarioUseCase.execute(any(Usuario.class), eq(session)))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioService.registerUser(dto))
                .expectNextMatches(u -> u.getEmail().equals("jhondoe@mail.com") &&
                        u.getPasswordHash().equals("hashed-pass"))
                .verifyComplete();
    }

    @Test
    void registerUserShouldFailWhenPermissionDenied() {
        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(session));
        when(sessionValidator.validatePermission(session, Permission.REGISTRAR_USUARIO))
                .thenReturn(Mono.error(new ForbiddenException()));

        StepVerifier.create(usuarioService.registerUser(dto))
                .expectError(ForbiddenException.class)
                .verify();

        verifyNoInteractions(registrarUsuarioUseCase);
    }

    @Test
    void registerUserShouldFailWhenPasswordEncodingFails() {
        Usuario mappedUser = usuario.toBuilder().passwordHash(null).build();

        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(session));
        when(sessionValidator.validatePermission(session, Permission.REGISTRAR_USUARIO)).thenReturn(Mono.empty());
        when(usuarioDtoMapper.toModel(dto)).thenReturn(mappedUser);
        when(passwordEncoderService.encodeReactive(dto.getPassword()))
                .thenReturn(Mono.error(new RuntimeException("Encoder failed")));

        StepVerifier.create(usuarioService.registerUser(dto))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Encoder failed"))
                .verify();
    }

    @Test
    void registerUserShouldFailWhenUseCaseFails() {
        Usuario mappedUser = usuario.toBuilder().passwordHash(null).build();

        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(session));
        when(sessionValidator.validatePermission(session, Permission.REGISTRAR_USUARIO)).thenReturn(Mono.empty());
        when(usuarioDtoMapper.toModel(dto)).thenReturn(mappedUser);
        when(passwordEncoderService.encodeReactive(dto.getPassword())).thenReturn(Mono.just("hashed-pass"));
        when(registrarUsuarioUseCase.execute(any(Usuario.class), eq(session)))
                .thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(usuarioService.registerUser(dto))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("DB down"))
                .verify();
    }
}

