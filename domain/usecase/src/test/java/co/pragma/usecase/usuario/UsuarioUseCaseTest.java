package co.pragma.usecase.usuario;

import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SalarioRangeValidator salarioRangeValidator;

    @Mock
    private UniqueEmailValidator uniqueEmailValidator;

    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioUseCase = new UsuarioUseCase(
                usuarioRepository,
                salarioRangeValidator,
                uniqueEmailValidator
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Usuario usuario = Usuario.builder()
                .id("1")
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@example.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(uniqueEmailValidator.validate(usuario)).thenReturn(Mono.just(usuario));
        when(salarioRangeValidator.validate(usuario)).thenReturn(Mono.just(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.registerUser(usuario))
                .expectNextMatches(u -> u.getEmail().equals("jhondoe@example.co"))
                .verifyComplete();

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void shouldfindByDocumentoSuccessfully() {
        String numeroDocumento = "123456789";
        String tipoDocumento = "CC";

        Usuario usuario = Usuario.builder()
                .id("1")
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@mail.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(usuarioRepository.findByDocumento(numeroDocumento, tipoDocumento)).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.findByDocumento(numeroDocumento, tipoDocumento))
                .expectNextMatches(u -> u.getEmail().equals("jhondoe@mail.co"))
                .verifyComplete();
        verify(usuarioRepository).findByDocumento(numeroDocumento, tipoDocumento);
    }
}
