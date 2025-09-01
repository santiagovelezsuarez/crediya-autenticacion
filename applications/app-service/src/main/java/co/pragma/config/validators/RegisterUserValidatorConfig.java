package co.pragma.config.validators;

import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

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
}

