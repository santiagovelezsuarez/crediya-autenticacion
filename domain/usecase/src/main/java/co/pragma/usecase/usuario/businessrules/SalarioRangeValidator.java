package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.SalarioBaseException;
import co.pragma.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SalarioRangeValidator {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private final BigDecimal maxSalary;

    public Mono<Usuario> validate(BigDecimal salario) {
        if (salario.compareTo(MIN_SALARY) <= 0)
            return Mono.error(new SalarioBaseException("El salario base debe ser mayor a 0"));

        if (salario.compareTo(maxSalary) > 0)
            return Mono.error(new SalarioBaseException("El salario base no puede ser mayor a " + maxSalary));

        return Mono.empty();
    }
}
