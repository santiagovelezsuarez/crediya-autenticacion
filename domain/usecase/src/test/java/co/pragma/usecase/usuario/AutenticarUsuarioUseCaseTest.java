package co.pragma.usecase.usuario;

import co.pragma.exception.business.AuthenticationException;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .email("test@mail.co")
                .passwordHash("**/hashedPassword/**")
                .build();
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        when(usuarioRepository.findByEmail("test@mail.com"))
                .thenReturn(Mono.just(usuario));
        when(passwordEncoderService.matches("password", "**/hashedPassword/**"))
                .thenReturn(true);

        StepVerifier.create(autenticarUsuarioUseCase.execute("test@mail.com", "password"))
                .expectNextMatches(u -> u.getEmail().equals("test@mail.co"))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(usuarioRepository.findByEmail("notfound@mail.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(autenticarUsuarioUseCase.execute("notfound@mail.com", "rawPass"))
                .expectError(AuthenticationException.class)
                .verify();
    }

    @Test
    void shouldFailWhenPasswordDoesNotMatch() {
        when(usuarioRepository.findByEmail("test@mail.co"))
                .thenReturn(Mono.just(usuario));
        when(passwordEncoderService.matches("wrong_password", "**/hashedPassword/**"))
                .thenReturn(false);

        StepVerifier.create(autenticarUsuarioUseCase.execute("test@mail.co", "wrong_password"))
                .expectError(AuthenticationException.class)
                .verify();
    }

    @Test
    void shouldPropagateUnexpectedRepositoryError() {
        when(usuarioRepository.findByEmail("test@mail.com"))
                .thenReturn(Mono.error(new RuntimeException("DB down")));

        StepVerifier.create(autenticarUsuarioUseCase.execute("test@mail.com", "rawPass"))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB down"))
                .verify();
    }
}
