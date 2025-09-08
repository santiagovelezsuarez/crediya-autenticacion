package co.pragma.api.handler.service;

import co.pragma.api.dto.AutenticarUsuarioDTO;
import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.security.JwtService;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.SessionProvider;
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

import java.util.Collections;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Mock
    private UsuarioDtoMapper usuarioDtoMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private SessionProvider sessionProvider;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private Validator validator;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        usuarioService = new UsuarioService(
                registrarUsuarioUseCase,
                usuarioDtoMapper,
                validator,
                jwtService,
                sessionProvider,
                passwordEncoderService
        );
    }

    @Test
    void registerUserShouldHashPasswordAndRegister() {
        RegistrarUsuarioDTO dto = new RegistrarUsuarioDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("plain-pass");

        Usuario mappedUser = Usuario.builder()
                .email("test@mail.com")
                .build();

        Usuario hashedUser = mappedUser.toBuilder()
                .passwordHash("hashed-pass")
                .build();

        Session fakeSession = Session.builder()
                .userId("admin-id")
                .role("ADMIN")
                .build();

        Usuario savedUser = hashedUser.toBuilder().id(UUID.randomUUID()).build();

        when(usuarioDtoMapper.toModel(dto)).thenReturn(mappedUser);
        when(passwordEncoderService.encodeReactive("plain-pass")).thenReturn(Mono.just("hashed-pass"));
        when(sessionProvider.getCurrentSession()).thenReturn(Mono.just(fakeSession));
        when(registrarUsuarioUseCase.registerUser(hashedUser, fakeSession))
                .thenReturn(Mono.just(savedUser));

        StepVerifier.create(usuarioService.registerUser(dto))
                .assertNext(user -> assertThat(user.getPasswordHash()).isEqualTo("hashed-pass"))
                .verifyComplete();

        verify(passwordEncoderService).encodeReactive("plain-pass");
        verify(registrarUsuarioUseCase).registerUser(any(Usuario.class), any());
    }

    @Test
    void authenticateShouldReturnAuthResultWithToken() {
        AutenticarUsuarioDTO dto = new AutenticarUsuarioDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("plain-pass");

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email("test@mail.com")
                .build();

        when(registrarUsuarioUseCase.authenticate("test@mail.com", "plain-pass")).thenReturn(Mono.just(usuario));
        when(jwtService.generateToken(usuario)).thenReturn("jwt-token");

        StepVerifier.create(usuarioService.authenticate(dto))
                .assertNext(authResult -> {
                    assertThat(authResult.usuario()).isEqualTo(usuario);
                    assertThat(authResult.token()).isEqualTo("jwt-token");
                })
                .verifyComplete();

        verify(jwtService).generateToken(usuario);
    }

    @Test
    void authenticateShouldErrorWhenInvalidDto() {
        AutenticarUsuarioDTO dto = new AutenticarUsuarioDTO();
        dto.setEmail("invalid_email");
        dto.setPassword("8888888");

        StepVerifier.create(usuarioService.authenticate(dto))
                .expectErrorMatches(RuntimeException.class::isInstance)
                .verify();
    }
}

