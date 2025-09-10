package co.pragma.usecase.usuario.businesrules;

import co.pragma.exception.business.DocumentoIdentidadAlreadyRegisteredException;
import co.pragma.model.usuario.TipoDocumento;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueDocumentoIdentidadValidatorTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UniqueDocumentoIdentidadValidator validator;

    @Test
    void validateShouldCompleteWhenDocumentoDoesNotExist() {
        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "123456"))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate("CC", "123456"))
                .verifyComplete();

        verify(usuarioRepository).findByTipoDocumentoAndNumeroDocumento("CC", "123456");
    }

    @Test
    void validateShouldErrorWhenDocumentoAlreadyExists() {
        Usuario existingUser = Usuario.builder()
                .tipoDocumento(TipoDocumento.CC)
                .numeroDocumento("123456")
                .build();

        when(usuarioRepository.findByTipoDocumentoAndNumeroDocumento("CC", "123456"))
                .thenReturn(Mono.just(existingUser));

        StepVerifier.create(validator.validate("CC", "123456"))
                .expectError(DocumentoIdentidadAlreadyRegisteredException.class)
                .verify();

        verify(usuarioRepository).findByTipoDocumentoAndNumeroDocumento("CC", "123456");
    }
}
