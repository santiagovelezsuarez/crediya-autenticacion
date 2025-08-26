package co.pragma.config.validators;

import co.pragma.model.usuario.Usuario;
import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;

import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import co.pragma.usecase.usuario.businessrules.UsuarioValidationPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class RegisterUserValidatorConfig {

    @Bean
    public SalarioRangeValidator salaryRangeValidator(@Value("${validation.salary.max}") BigDecimal maxSalary) {
        return new SalarioRangeValidator(maxSalary);
    }

    @Bean
    public UniqueEmailValidator uniqueEmailValidator(UsuarioRepository repository) {
        return new UniqueEmailValidator(repository);
    }

    @Bean
    @Primary
    public BusinessValidator<Usuario> registrationValidator(SalarioRangeValidator salaryRangeValidator, UniqueEmailValidator uniqueEmailValidator) {
        return new UsuarioValidationPolicy(List.of(
                salaryRangeValidator,
                uniqueEmailValidator
        ));
    }
}

