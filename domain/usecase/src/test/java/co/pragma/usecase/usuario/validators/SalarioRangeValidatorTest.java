package co.pragma.usecase.usuario.validators;

import co.pragma.common.exception.BusinessException;
import co.pragma.model.usuario.Usuario;
import co.pragma.usecase.usuario.criteria.validators.SalarioRangeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class SalarioRangeValidatorTest {

    private final BigDecimal MAX_SALARY = BigDecimal.valueOf(15000000);

    @Test
    void shouldPassWhenSalaryIsWithinRange() {
        Usuario usuario = Usuario.builder()
                .salarioBase(BigDecimal.valueOf(10000000))
                .build();

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(usuario))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void shouldFailWhenSalaryIsZeroOrNegative() {
        BigDecimal[] invalidSalaries = {BigDecimal.ZERO, BigDecimal.valueOf(-100)};

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        for (BigDecimal salary : invalidSalaries) {
            Usuario usuario = Usuario.builder().salarioBase(salary).build();
            StepVerifier.create(validator.validate(usuario))
                    .expectErrorMatches(ex -> ex instanceof BusinessException &&
                            ex.getMessage().toLowerCase().contains("mayor a 0"))
                    .verify();
        }
    }

    @Test
    void shouldFailWhenSalaryExceedsMax() {
        Usuario usuario = Usuario.builder()
                .salarioBase(BigDecimal.valueOf(20000000))
                .build();

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(usuario))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().toLowerCase().contains("mayor a"))
                .verify();
    }

    @Test
    void shouldPassWhenSalaryEqualsMax() {
        Usuario usuario = Usuario.builder()
                .salarioBase(MAX_SALARY)
                .build();

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(usuario))
                .expectNext(usuario)
                .verifyComplete();
    }
}
