package co.pragma.config.beans;

import co.pragma.model.session.PermissionValidator;
import co.pragma.api.security.SecurityContextSessionProvider;
import co.pragma.model.usuario.gateways.SessionProvider;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueDocumentoIdentidadValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class UserBeansConfig {

    @Bean
    public SalarioRangeValidator salaryRangeValidator(@Value("${validation.salary.max}") BigDecimal maxSalary) {
        return new SalarioRangeValidator(maxSalary);
    }

    @Bean
    public UniqueEmailValidator uniqueEmailValidator(UsuarioRepository repository) {
        return new UniqueEmailValidator(repository);
    }

    @Bean
    public UniqueDocumentoIdentidadValidator uniqueDocumentoIdentidad(UsuarioRepository repository) {
        return new UniqueDocumentoIdentidadValidator(repository);
    }

    @Bean
    public SessionProvider sessionProvider() {
        return new SecurityContextSessionProvider();
    }

    @Bean
    public PermissionValidator permissionValidator(SessionProvider sessionProvider) {
        return new PermissionValidator(sessionProvider);
    }
}

