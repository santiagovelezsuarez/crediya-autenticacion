package co.pragma.usecase.usuario.validators;

import co.pragma.common.exception.BusinessException;
import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.criteria.ValidationChain;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.mockito.Mockito.*;

public class ValidationChainTest {

    @Test
    void shouldPassWhenAllValidatorsPass() {
        Usuario usuario = Usuario.builder()
                .salarioBase(BigDecimal.valueOf(1000000))
                .email("user@example.co")
                .build();

        BusinessValidator<Usuario> v1 = mock(BusinessValidator.class);
        BusinessValidator<Usuario> v2 = mock(BusinessValidator.class);

        when(v1.validate(usuario)).thenReturn(Mono.just(usuario));
        when(v2.validate(usuario)).thenReturn(Mono.just(usuario));

        ValidationChain chain = new ValidationChain(Arrays.asList(v1, v2));

        StepVerifier.create(chain.validate(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(v1).validate(usuario);
        verify(v2).validate(usuario);
    }

    @Test
    void shouldFailWhenAnyValidatorFails() {
        Usuario usuario = Usuario.builder()
                .salarioBase(BigDecimal.valueOf(1000000))
                .email("user@example.co")
                .build();

        BusinessValidator<Usuario> v1 = mock(BusinessValidator.class);
        BusinessValidator<Usuario> v2 = mock(BusinessValidator.class);

        when(v1.validate(usuario)).thenReturn(Mono.error(new BusinessException("Error")));
        when(v2.validate(usuario)).thenReturn(Mono.just(usuario));

        ValidationChain chain = new ValidationChain(Arrays.asList(v1, v2));

        StepVerifier.create(chain.validate(usuario))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().equals("Error"))
                .verify();

        verify(v1).validate(usuario);
        verify(v2, never()).validate(usuario);
    }
}
