package co.pragma.api.dto;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

class DtoValidatorBuilderTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (var factory = buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldReturnDtoWhenValid() {
        UsuarioRequest dto = UsuarioRequest.builder()
                .nombres("John")
                .apellidos("Doe")
                .tipoDocumento("CC")
                .numeroDocumento("123456")
                .email("john.doe@mail.com")
                .salarioBase(BigDecimal.valueOf(2000))
                .build();

        StepVerifier.create(DtoValidatorBuilder.validate(dto, validator))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void shouldErrorWhenInvalidEmail() {
        UsuarioRequest dto = UsuarioRequest.builder()
                .nombres("John")
                .apellidos("Doe")
                .tipoDocumento("CC")
                .numeroDocumento("123")
                .email("INVALID_EMAIL")
                .salarioBase(BigDecimal.valueOf(1000))
                .build();

        StepVerifier.create(DtoValidatorBuilder.validate(dto, validator))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DtoValidationException.class);

                    DtoValidationException ex = (DtoValidationException) error;

                    assertThat(ex.getErrors())
                            .anySatisfy(fieldError -> {
                                assertThat(fieldError.field()).isEqualTo("email");
                            });
                })
                .verify();
    }

    @Test
    void shouldErrorWhenMultipleFieldsInvalid() {
        UsuarioRequest dto = UsuarioRequest.builder()
                .nombres("")
                .apellidos("")
                .tipoDocumento("XX")
                .numeroDocumento("")
                .email("not-an-email")
                .salarioBase(BigDecimal.valueOf(-1000))
                .build();

        StepVerifier.create(DtoValidatorBuilder.validate(dto, validator))
                .expectErrorSatisfies(error -> {
                    assert error instanceof DtoValidationException;
                    DtoValidationException ex = (DtoValidationException) error;

                    assert ex.getErrors().size() >= 3;

                    assert ex.getErrors().stream()
                            .anyMatch(e -> e.field().equals("salarioBase"));
                })
                .verify();
    }
}
