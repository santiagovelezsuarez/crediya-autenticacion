package co.pragma.usecase.usuario.validators;

import co.pragma.exception.BusinessException;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniqueEmailValidatorTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Usuario usuario = Usuario.builder()
                .email("jhondoe@example.co")
                .build();

        UniqueEmailValidator validator = new UniqueEmailValidator(usuarioRepository);

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(validator.validate(usuario))
                .expectErrorMatches(ex -> ex instanceof BusinessException)
                .verify();
    }

    @Test
    void shouldPassWhenEmailDoesNotExist() {
        Usuario usuario = Usuario.builder()
                .email("newuser@example.co")
                .build();

        UniqueEmailValidator validator = new UniqueEmailValidator(usuarioRepository);

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(usuario))
                .expectNext(usuario)
                .verifyComplete();
    }
}
