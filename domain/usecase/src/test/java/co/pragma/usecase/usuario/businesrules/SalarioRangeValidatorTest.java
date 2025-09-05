package co.pragma.usecase.usuario.businesrules;

import co.pragma.exception.SalarioBaseException;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class SalarioRangeValidatorTest {

    private final BigDecimal MAX_SALARY = BigDecimal.valueOf(15000000);

    @Test
    void shouldPassWhenSalarioIsWithinRange() {
        BigDecimal salario = BigDecimal.valueOf(5000000);

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(salario))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenSalarioIsNegative() {
        BigDecimal invalidSalario = BigDecimal.valueOf(-0.5);

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(invalidSalario))
                .expectErrorMatches(SalarioBaseException.class::isInstance)
                .verify();
    }

    @Test
    void shouldFailWhenSalarioExceedsMax() {
        BigDecimal salario = BigDecimal.valueOf(15000000.01);

        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(salario))
                .expectErrorMatches(SalarioBaseException.class::isInstance)
                .verify();
    }

    @Test
    void shouldPassWhenSalarioEqualsMax() {
        SalarioRangeValidator validator = new SalarioRangeValidator(MAX_SALARY);

        StepVerifier.create(validator.validate(MAX_SALARY))
                .verifyComplete();
    }
}
