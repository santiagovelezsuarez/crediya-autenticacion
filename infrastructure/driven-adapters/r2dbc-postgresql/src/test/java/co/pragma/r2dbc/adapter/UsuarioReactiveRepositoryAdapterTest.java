package co.pragma.r2dbc.adapter;

import co.pragma.exception.ErrorCode;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioReactiveRepositoryAdapterTest {

    @Mock
    UsuarioReactiveRepository usuarioRepository;

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
                .id(UUID.randomUUID())
                .tipoDocumento("CC")
                .numeroDocumento("789")
                .nombres("Pepe")
                .email("pepe@mail.co")
                .idRol(1)
                .build();

        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("789")
                .nombres("Pepe")
                .email("pepe@mail.co")
                .rol(rol)
                .build();
    }

    @Test
    void shouldReturnUsuarioWithRoleWhenSaveSuccessful() {
        when(mapper.toEntity(usuario)).thenReturn(entity);
        when(usuarioRepository.save(entity)).thenReturn(Mono.just(entity));
        when(rolRepository.findById(1)).thenReturn(Mono.just(rol));
        when(mapper.toDomainWithRole(any(UsuarioEntity.class), eq(rol)))
                .thenReturn(usuario);
        when(mapper.toDomainWithRole(any(UsuarioEntity.class), eq(null)))
                .thenReturn(usuario);


        StepVerifier.create(adapter.save(usuario))
                .expectNextMatches(u -> u.getRol().getNombre().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUsuarioWhenDocumentoAndNumeroFound() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .thenReturn(Mono.just(entity));
        when(rolRepository.findById(1))
                .thenReturn(Mono.just(rol));
        when(mapper.toDomainWithRole(any(UsuarioEntity.class), eq(rol)))
                .thenReturn(usuario);
        when(mapper.toDomainWithRole(any(UsuarioEntity.class), eq(null)))
                .thenReturn(usuario);


        StepVerifier.create(adapter.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .expectNextMatches(u -> u.getNumeroDocumento().equals("789"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenDocumentoAndNumeroNotFound() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByTipoDocumentoAndNumeroDocumento("CC", "789"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUsuarioWhenEmailExists() {
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

        StepVerifier.create(adapter.findByEmail("notfound@mail.com"))
                .verifyComplete();
    }

    @Test
    void shouldWrapRepositoryErrorInInfrastructureException() {
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findByEmail("error@mail.com"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(InfrastructureException.class);
                    assertThat(error.getMessage()).isEqualTo(ErrorCode.DB_ERROR.name());
                })
                .verify();
    }
}
