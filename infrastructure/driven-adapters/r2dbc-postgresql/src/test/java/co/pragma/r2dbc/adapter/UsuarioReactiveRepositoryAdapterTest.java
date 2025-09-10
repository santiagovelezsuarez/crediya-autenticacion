package co.pragma.r2dbc.adapter;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.rol.Rol;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.r2dbc.entity.UsuarioEntity;
import co.pragma.r2dbc.mapper.UsuarioEntityMapper;
import co.pragma.r2dbc.repository.UsuarioReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {

    @Mock
    UsuarioReactiveRepository  usuarioRepository;

    @Mock
    RolReactiveRepositoryAdapter rolRepository;

    @Mock
    UsuarioEntityMapper mapper;

    @Mock
    DatabaseClient databaseClient;

    @InjectMocks
    UsuarioReactiveRepositoryAdapter adapter;

    private UsuarioEntity entity;
    private Usuario usuario;
    private Rol rol;


    @BeforeEach
    void setUp() {
        rol = Rol.builder().id(1).nombre("ADMIN").build();
        entity = UsuarioEntity.builder()
                .tipoDocumento("CC")
                .numeroDocumento("789")
                .nombres("Pepe")
                .email("pepe@mail.co")
                .idRol(1)
                .build();
        usuario = Usuario.builder()
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("789")
                .nombres("Pepe")
                .email("pepe@mail.co")
                .rol(rol)
                .build();
    }

    @Test
    void shouldReturnUserWithRoleWhenSaveSuccessful() {
        when(mapper.toEntity(usuario)).thenReturn(entity);
        when(usuarioRepository.save(entity)).thenReturn(Mono.just(entity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));
        when(mapper.toDomain(entity, rol)).thenReturn(usuario.toBuilder().rol(rol).build());
        when(mapper.toDomain(entity, null)).thenReturn(usuario);

        StepVerifier.create(adapter.save(usuario))
                .expectNextMatches(u -> u.getRol().getNombre().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUsuarioWhenFound() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "789")).thenReturn(Mono.just(entity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));
        when(mapper.toDomain(entity, rol)).thenReturn(usuario);
        when(mapper.toDomain(entity, null)).thenReturn(usuario);

        StepVerifier.create(adapter.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .expectNextMatches(u -> u.getNumeroDocumento().equals("789"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUserWhenEmailExists() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(entity));
        when(rolRepository.findById(1)).thenReturn(Mono.empty());
        when(mapper.toDomain(entity, null)).thenReturn(usuario);

        StepVerifier.create(adapter.findByEmail("pepe@mail.co"))
                .expectNextMatches(u -> u.getEmail().equals("pepe@mail.co"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        Mono<Usuario> result = adapter.findByEmail("notfound@mail.com");

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenRepositoryFails() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.error(new RuntimeException("DB error")));

        Mono<Usuario> result = adapter.findByEmail("error@mail.com");

        StepVerifier.create(result)
                .expectErrorMatches(RuntimeException.class::isInstance)
                .verify();
    }

    @Test
    void shouldMapDbErrorToInfrastructureException() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.error(new RuntimeException("bad SQL grammar")));

        Mono<Usuario> result = adapter.findByEmail("user@mail.com");

        StepVerifier.create(result).expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InfrastructureException.class);
                    assertThat(error.getMessage()).isEqualTo(ErrorCode.DB_ERROR.name());
                })
                .verify();
    }
}
