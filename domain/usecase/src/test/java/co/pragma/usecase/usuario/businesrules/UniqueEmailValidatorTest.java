package co.pragma.usecase.usuario.businesrules;

import co.pragma.exception.business.EmailAlreadyRegisteredException;
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
class UniqueEmailValidatorTest {

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

        StepVerifier.create(validator.validate(usuario.getEmail()))
                .expectError(EmailAlreadyRegisteredException.class)
                .verify();
    }

    @Test
    void shouldPassWhenEmailDoesNotExist() {
        String email = "new_user@mail.co";

        UniqueEmailValidator validator = new UniqueEmailValidator(usuarioRepository);

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(email))
                .expectNext()
                .verifyComplete();
    }
}
