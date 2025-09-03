package co.pragma.usecase.usuario.businessrules;

import co.pragma.exception.SalarioBaseException;
import co.pragma.gateways.BusinessValidator;
import co.pragma.model.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SalarioRangeValidator implements BusinessValidator<Usuario> {

    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private final BigDecimal maxSalary;

    @Override
    public Mono<Usuario> validate(Usuario usuario) {
        BigDecimal salary = usuario.getSalarioBase();

        if (salary.compareTo(MIN_SALARY) <= 0) {
            return Mono.error(new SalarioBaseException("El salario base debe ser mayor a 0"));
        }
        if (salary.compareTo(maxSalary) > 0) {
            return Mono.error(new SalarioBaseException("El salario base no puede ser mayor a " + maxSalary));
        }

        return Mono.just(usuario);
    }
}
