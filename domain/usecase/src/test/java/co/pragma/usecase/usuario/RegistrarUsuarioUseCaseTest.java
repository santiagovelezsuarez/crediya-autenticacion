package co.pragma.usecase.usuario;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.RolEnum;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.Session;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
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
    private Session session;

    @InjectMocks
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Test
    void shouldRegisterUserSuccessfully() {
        Rol adminRol = Rol.builder().id(1).nombre("ADMIN").build();

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
                .rol(adminRol) // rol inicial
                .build();

        when(uniqueEmailValidator.validate(usuario.getEmail())).thenReturn(Mono.empty());
        when(salarioRangeValidator.validate(usuario.getSalarioBase())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(usuario.getTipoDocumento().name(), usuario.getNumeroDocumento())).thenReturn(Mono.empty());
        when(rolRepository.findByNombre("ADMIN")).thenReturn(Mono.just(adminRol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(session.getRole()).thenReturn(RolEnum.ADMIN.getNombre());

        StepVerifier.create(registrarUsuarioUseCase.execute(usuario, session))
                .expectNextMatches(u -> u.getEmail().equals("jhondoe@example.co") &&
                        u.getRol().getNombre().equals("ADMIN"))
                .verifyComplete();

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registerUserShouldFailWhenSessionNotAdmin() {
        Usuario usuario = Usuario.builder()
                .email("user@mail.com")
                .salarioBase(BigDecimal.valueOf(1000))
                .rol(Rol.builder().nombre(RolEnum.CLIENTE.getNombre()).build())
                .build();

        when(session.getRole()).thenReturn(RolEnum.CLIENTE.getNombre());

        StepVerifier.create(registrarUsuarioUseCase.execute(usuario, session))
                .expectError(ForbiddenException.class)
                .verify();
    }
}

