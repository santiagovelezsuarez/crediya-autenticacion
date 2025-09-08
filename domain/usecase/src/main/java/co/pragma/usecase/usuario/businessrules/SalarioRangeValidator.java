package co.pragma.usecase.usuario.businessrules;

import co.pragma.error.ErrorMessages;
import co.pragma.exception.business.SalarioBaseException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SalarioRangeValidator {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private final BigDecimal maxSalary;

    public Mono<Void> validate(BigDecimal salario) {
        if (salario.compareTo(MIN_SALARY) <= 0)
            return Mono.error(new SalarioBaseException(ErrorMessages.SALARY_OUT_OF_RANGE_MIN));

        if (salario.compareTo(maxSalary) > 0)
            return Mono.error(new SalarioBaseException(ErrorMessages.SALARY_OUT_OF_RANGE_MAX + maxSalary));

        return Mono.empty();
    }
}
