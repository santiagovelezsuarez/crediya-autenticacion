package co.pragma.r2dbc.adapter;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.rol.Rol;
import co.pragma.r2dbc.entity.RolEntity;
import co.pragma.r2dbc.repository.RolReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolReactiveRepositoryAdapterTest {

    @Mock
    private RolReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private RolReactiveRepositoryAdapter adapter;

    private RolEntity rolEntity;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rolEntity = new RolEntity(1, "ADMIN", "Administrador");
        rol = new Rol(1, "ADMIN", "Administrador");
    }

    @Test
    void findByIdShouldReturnMappedRol() {
        when(repository.findById(1)).thenReturn(Mono.just(rolEntity));
        when(mapper.map(rolEntity, Rol.class)).thenReturn(rol);

        StepVerifier.create(adapter.findById(1))
                .assertNext(result -> assertThat(result).isEqualTo(rol))
                .verifyComplete();
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(repository.findById(99)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99))
                .verifyComplete();
    }


    @Test
    void findByNombreShouldReturnMappedRol() {
        when(repository.findByNombre("ADMIN")).thenReturn(Mono.just(rolEntity));
        when(mapper.map(rolEntity, Rol.class)).thenReturn(rol);

        StepVerifier.create(adapter.findByNombre("ADMIN"))
                .assertNext(result -> assertThat(result).isEqualTo(rol))
                .verifyComplete();
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenFindByIdails() {
        when(repository.findById(anyInt())).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findById(1))
                .expectErrorMatches(throwable ->
                        throwable instanceof InfrastructureException &&
                        throwable.getMessage().equals(ErrorCode.DB_ERROR.name())
                )
                .verify();

        verify(repository).findById(1);
    }

    @Test
    void shouldReturnInfrastructureExceptionWhenFindByNombreFails() {
        when(repository.findByNombre(any(String.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findByNombre("ADMIN"))
                .expectErrorMatches(throwable ->
                        throwable instanceof InfrastructureException &&
                        throwable.getMessage().contains(ErrorCode.DB_ERROR.name())
                )
                .verify();

        verify(repository).findByNombre("ADMIN");
    }
}
