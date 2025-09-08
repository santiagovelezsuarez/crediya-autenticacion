package co.pragma.usecase.usuario.businesrules;

import co.pragma.exception.business.RolNotFoundException;
import co.pragma.model.rol.Rol;
import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.usecase.usuario.businessrules.RolResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolResolverTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolResolver rolResolver;

    @Test
    void resolveShouldReturnRolWhenFound() {
        Rol rol = Rol.builder().nombre("ADMIN").build();

        when(rolRepository.findByNombre("ADMIN")).thenReturn(Mono.just(rol));

        StepVerifier.create(rolResolver.resolve("ADMIN"))
                .assertNext(foundRol -> assertThat(foundRol.getNombre()).isEqualTo("ADMIN"))
                .verifyComplete();

        verify(rolRepository).findByNombre("ADMIN");
    }

    @Test
    void resolveShouldErrorWhenRolNotFound() {
        when(rolRepository.findByNombre("QUARKUS")).thenReturn(Mono.empty());

        StepVerifier.create(rolResolver.resolve("QUARKUS"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(RolNotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Rol no encontrado: QUARKUS");
                })
                .verify();

        verify(rolRepository).findByNombre("QUARKUS");
    }
}
