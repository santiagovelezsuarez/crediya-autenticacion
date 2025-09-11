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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        when(mapper.toDomainWithRole(entity, rol)).thenReturn(usuario.toBuilder().rol(rol).build());
        when(mapper.toDomainWithRole(entity, null)).thenReturn(usuario);

        StepVerifier.create(adapter.save(usuario))
                .expectNextMatches(u -> u.getRol().getNombre().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUsuarioWhenFound() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "789")).thenReturn(Mono.just(entity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));
        when(mapper.toDomainWithRole(entity, rol)).thenReturn(usuario);
        when(mapper.toDomainWithRole(entity, null)).thenReturn(usuario);

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
        when(mapper.toDomainWithRole(entity, null)).thenReturn(usuario);

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

    @Test
    void shouldReturnUsuarios_WhenFound() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        List<UUID> userIds = List.of(userId1, userId2);

        UsuarioEntity entity1 = UsuarioEntity.builder().id(userId1).email("user1@mail.com").build();
        UsuarioEntity entity2 = UsuarioEntity.builder().id(userId2).email("user2@mail.com").build();
        Usuario usuario1 = Usuario.builder().id(userId1).email("user1@mail.com").build();
        Usuario usuario2 = Usuario.builder().id(userId2).email("user2@mail.com").build();

        when(usuarioRepository.findByIdIn(userIds)).thenReturn(Flux.just(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(usuario1);
        when(mapper.toDomain(entity2)).thenReturn(usuario2);

        StepVerifier.create(adapter.findByIdIn(userIds))
                .expectNext(usuario1)
                .expectNext(usuario2)
                .verifyComplete();
    }

    @Test
    void findByIdIn_shouldReturnEmptyFlux_whenRepositoryReturnsEmpty() {
        List<UUID> userIds = List.of(UUID.randomUUID());
        when(usuarioRepository.findByIdIn(userIds)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findByIdIn(userIds))
                .verifyComplete();
    }

    @Test
    void shouldReturnInfrastructureException_whenFindByIdInFails() {
        List<UUID> userIds = List.of(UUID.randomUUID());
        when(usuarioRepository.findByIdIn(any())).thenReturn(Flux.error(new RuntimeException("DB connection error")));

        StepVerifier.create(adapter.findByIdIn(userIds))
                .expectErrorMatches(throwable ->
                        throwable instanceof InfrastructureException &&
                                throwable.getMessage().equals(ErrorCode.DB_ERROR.name()))
                .verify();
    }
}
