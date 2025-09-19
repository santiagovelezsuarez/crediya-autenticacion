package co.pragma.usecase.usuario;

import co.pragma.exception.ErrorCode;
import co.pragma.exception.business.BusinessException;
import co.pragma.exception.business.RolNotFoundException;
import co.pragma.exception.business.SalarioBaseException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.command.RegistrarUsuarioCommand;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository userRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private SalarioRangeValidator salarioRangeValidator;
    @Mock
    private UniqueEmailValidator uniqueEmailValidator;
    @Mock
    private UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidadValidator;
    @Mock
    private PasswordEncoderService passwordEncoder;

    @InjectMocks
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private RegistrarUsuarioCommand command;
    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        command = RegistrarUsuarioCommand.builder()
                .nombres("Juan")
                .apellidos("Perez")
                .tipoDocumento("CC")
                .numeroDocumento("1234567")
                .email("juan.perez@example.com")
                .rawPassword("12345678")
                .rol("ASESOR")
                .salarioBase(BigDecimal.valueOf(5000000))
                .build();

        rol = Rol.builder().nombre("ASESOR").build();
        usuario = Usuario.builder().nombres("Juan").rol(rol).build();
    }

    @Test
    void shouldRegisterUserWhenAllRulesPass() {
        when(salarioRangeValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueEmailValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(any(), any())).thenReturn(Mono.empty());
        when(passwordEncoder.encodeReactive(any())).thenReturn(Mono.just("hashedPassword"));
        when(rolRepository.findByNombre(any())).thenReturn(Mono.just(rol));
        when(userRepository.save(any(Usuario.class))).thenReturn(Mono.just(usuario));

        StepVerifier.create(registrarUsuarioUseCase.execute(command))
                .expectNextMatches(savedUser ->
                        savedUser.getNombres().equals("Juan") &&
                                savedUser.getRol().getNombre().equals("ASESOR")
                )
                .verifyComplete();
    }

    @Test
    void shouldThrowBusinessExceptionWhenSalarioIsInvalid() {
        when(salarioRangeValidator.validate(any())).thenReturn(Mono.error(new SalarioBaseException(ErrorCode.SALARIO_OUT_OF_RANGE)));

        when(uniqueEmailValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(any(), any())).thenReturn(Mono.empty());
        when(passwordEncoder.encodeReactive(any())).thenReturn(Mono.just("someHashedPassword"));

        StepVerifier.create(registrarUsuarioUseCase.execute(command))
                .expectErrorMatches(e -> e instanceof BusinessException && e.getMessage().equals(ErrorCode.SALARIO_OUT_OF_RANGE.getDefaultMessage()))
                .verify();
    }

    @Test
    void shouldThrowRolNotFoundExceptionWhenRolDoesNotExist() {
        when(salarioRangeValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueEmailValidator.validate(any())).thenReturn(Mono.empty());
        when(uniqueDocumentoIdentidadValidator.validate(any(), any())).thenReturn(Mono.empty());
        when(passwordEncoder.encodeReactive(any())).thenReturn(Mono.just("hashedPassword"));
        when(rolRepository.findByNombre(any())).thenReturn(Mono.empty());

        StepVerifier.create(registrarUsuarioUseCase.execute(command))
                .expectError(RolNotFoundException.class)
                .verify();
    }
}

