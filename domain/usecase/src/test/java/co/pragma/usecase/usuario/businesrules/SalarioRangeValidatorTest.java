package co.pragma.usecase.usuario.businesrules;

import co.pragma.exception.business.SalarioBaseException;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class SalarioRangeValidatorTest {

    private final BigDecimal maxSalary = BigDecimal.valueOf(15000000);
    private SalarioRangeValidator validator;

    @BeforeEach
    void setup() {
        validator = new SalarioRangeValidator(maxSalary);
    }

    @Test
    void shouldPassWhenSalarioIsWithinRange() {
        BigDecimal salario = BigDecimal.valueOf(5000000);

        StepVerifier.create(validator.validate(salario))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenSalarioIsNegative() {
        BigDecimal invalidSalario = BigDecimal.valueOf(-0.5);

        StepVerifier.create(validator.validate(invalidSalario))
                .expectErrorMatches(SalarioBaseException.class::isInstance)
                .verify();
    }

    @Test
    void shouldFailWhenSalarioExceedsMax() {
        BigDecimal salario = BigDecimal.valueOf(15000000.01);

        StepVerifier.create(validator.validate(salario))
                .expectErrorMatches(SalarioBaseException.class::isInstance)
                .verify();
    }

    @Test
    void shouldPassWhenSalarioEqualsMax() {
        StepVerifier.create(validator.validate(maxSalary))
                .verifyComplete();
    }
}
