package co.pragma.usecase.usuario;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.RolResolver;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SalarioRangeValidator salarioRangeValidator;

    @Mock
    private UniqueEmailValidator uniqueEmailValidator;

    @Mock
    private UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidadValidator;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private RolResolver rolResolver;

    @Mock
    private PasswordEncoderService passwordEncoder;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private Session session;

    @InjectMocks()
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Test
    void shouldRegisterUserSuccessfully() {
        Usuario usuario = Usuario.builder()
                .id(UUID.fromString("33ac0a47-79bd-4d78-8d08-c9c707cfa529"))
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@example.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("123456789")
                .rol(Rol.builder().nombre("ADMIN").build())
                .build();

        when(uniqueEmailValidator.validate(usuario.getEmail())).thenReturn(Mono.empty());
        when(salarioRangeValidator.validate(usuario.getSalarioBase())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(String.valueOf(usuario.getTipoDocumento()), usuario.getNumeroDocumento())).thenReturn(Mono.empty());
        when(rolResolver.resolve(usuario.getRol().getNombre())).thenReturn(Mono.just(usuario.getRol()));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));
        when(session.getRole()).thenReturn(RolEnum.ADMIN.getNombre());

        StepVerifier.create(registrarUsuarioUseCase.execute(usuario, session))
                .expectNextMatches(u -> u.getEmail().equals("jhondoe@example.co"))
                .verifyComplete();

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void registerUserShouldFailWhenSessionNotAdmin() {
        Usuario usuario = Usuario.builder()
                .email("user@mail.com")
                .salarioBase(BigDecimal.valueOf(1000))
                .rol(Rol.builder().nombre("USER").build())
                .build();

        when(session.getRole()).thenReturn("USER");

        StepVerifier.create(registrarUsuarioUseCase.execute(usuario, session))
                .expectError(ForbiddenException.class)
                .verify();
    }

    @Test
    void registerUserShouldMapUnexpectedErrorToInfrastructureException() {
        Usuario usuario = Usuario.builder()
                .email("user@mail.com")
                .salarioBase(BigDecimal.valueOf(1000))
                .rol(Rol.builder().nombre("ADMIN").build())
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("123456789")
                .build();

        when(session.getRole()).thenReturn(RolEnum.ADMIN.getNombre());
        when(salarioRangeValidator.validate(any())).thenReturn(Mono.error(new RuntimeException("DB down")));
        when(uniqueEmailValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(any(), any())).thenReturn(Mono.empty());
        when(rolResolver.resolve(any())).thenReturn(Mono.just(usuario.getRol()));

        StepVerifier.create(registrarUsuarioUseCase.execute(usuario, session))
                .expectError(InfrastructureException.class)
                .verify();
    }
}
