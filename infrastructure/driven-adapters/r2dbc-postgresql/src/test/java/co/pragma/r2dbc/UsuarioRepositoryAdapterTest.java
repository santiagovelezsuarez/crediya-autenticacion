package co.pragma.r2dbc;

import co.pragma.exception.EmailAlreadyRegisteredException;
import co.pragma.model.usuario.Usuario;
import co.pragma.r2dbc.entity.UsuarioEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {

    @InjectMocks
    UsuarioReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UsuarioReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void shouldSaveUser() {
        Usuario usuario = Usuario.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@example.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();
        UsuarioEntity entity = UsuarioEntity.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@example.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(repository.save(any())).thenReturn(Mono.just(entity));
        when(mapper.map(usuario, UsuarioEntity.class)).thenReturn(entity);
        when(mapper.map(entity, Usuario.class)).thenReturn(usuario);

        StepVerifier.create(repositoryAdapter.save(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(repository).save(any());
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "jhondoe@example.co";

        Usuario usuario = Usuario.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email(email)
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        UsuarioEntity entity = UsuarioEntity.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email(email)
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(repository.findByEmail(email)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Usuario.class)).thenReturn(usuario);

        StepVerifier.create(repositoryAdapter.findByEmail(email))
                .expectNext(usuario)
                .verifyComplete();

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldFindUserByDocumento() {
        String numeroDocumento = "123456789";
        String tipoDocumento = "CC";

        Usuario usuario = Usuario.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@email.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        UsuarioEntity entity = UsuarioEntity.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@email.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(repository.findByDocumento(tipoDocumento, numeroDocumento)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Usuario.class)).thenReturn(usuario);

        StepVerifier.create(repositoryAdapter.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento))
                .expectNext(usuario)
                .verifyComplete();

        verify(repository).findByDocumento(tipoDocumento, numeroDocumento);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        String email = "notfound@example.com";

        when(repository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByEmail(email))
                .verifyComplete();

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldErrorWhenEmailAlreadyRegistered() {
        Usuario usuario = Usuario.builder()
                .nombres("Jhon Doe")
                .apellidos("Doe")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .direccion("123 Main St")
                .telefono("1234567890")
                .email("jhondoe@mail.co")
                .salarioBase(BigDecimal.valueOf(3250000))
                .build();

        when(repository.save(any())).thenReturn(Mono.error(new EmailAlreadyRegisteredException("")));

        StepVerifier.create(repositoryAdapter.save(usuario))
                .expectError(EmailAlreadyRegisteredException.class)

                .verify();
    }

    @Test
    void shouldErrorWhenDatabaseErrorOnFindByEmail() {
        String email = "jhondoe@mail.co";

        when(repository.findByEmail(email)).thenReturn(Mono.error(new RuntimeException("Database error")));
        StepVerifier.create(repositoryAdapter.findByEmail(email))
                .expectError(RuntimeException.class)
                .verify();
        verify(repository).findByEmail(email);
    }

    @Test
    void shouldErrorWhenDatabaseErrorOnFindByTipoDocumentoAndNumeroDocumento() {
        String numeroDocumento = "123456789";
        String tipoDocumento = "CC";

        when(repository.findByDocumento(numeroDocumento, tipoDocumento)).thenReturn(Mono.error(new RuntimeException("Database error")));
        StepVerifier.create(repositoryAdapter.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento))
                .expectError(RuntimeException.class)
                .verify();
        verify(repository).findByDocumento(numeroDocumento, tipoDocumento);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByDocumento() {
        String numeroDocumento = "123456789";
        String tipoDocumento = "CC";

        when(repository.findByDocumento(numeroDocumento, tipoDocumento)).thenReturn(Mono.empty());

        StepVerifier.create(repositoryAdapter.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento))
                .verifyComplete();

        verify(repository).findByDocumento(numeroDocumento, tipoDocumento);
    }

}
