package co.pragma.config.validators;

import co.pragma.model.usuario.Usuario;
import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.user.criteria.validators.SalaryRangeValidator;
import co.pragma.usecase.user.criteria.validators.UniqueEmailValidator;
import co.pragma.usecase.user.criteria.ValidationChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class RegisterUserValidatorConfig {

    @Bean
    public SalaryRangeValidator salaryRangeValidator(@Value("${validation.salary.max}") BigDecimal maxSalary) {
        return new SalaryRangeValidator(maxSalary);
    }

    @Bean
    public UniqueEmailValidator uniqueEmailValidator(UsuarioRepository repository) {
        return new UniqueEmailValidator(repository);
    }

    @Bean
    @Primary
    public BusinessValidator<Usuario> registrationValidator(SalaryRangeValidator salaryRangeValidator, UniqueEmailValidator uniqueEmailValidator) {
        return new ValidationChain(List.of(
                salaryRangeValidator,
                uniqueEmailValidator
        ));
    }
}

