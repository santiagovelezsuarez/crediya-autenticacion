package co.pragma.config;

import co.pragma.model.rol.gateways.RolRepository;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.AutenticarUsuarioUseCase;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }

        @Bean
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }

        @Bean
        public RolRepository rolRepository() {
            return Mockito.mock(RolRepository.class);
        }

        @Bean
        public RegistrarUsuarioUseCase usuarioUseCase() {
            return Mockito.mock(RegistrarUsuarioUseCase.class);
        }

        @Bean
        public AutenticarUsuarioUseCase autenticarUsuarioUseCase() {
            return  Mockito.mock(AutenticarUsuarioUseCase.class);
        }

        @Bean
        public SalarioRangeValidator salarioRangeValidator() {
            return Mockito.mock(SalarioRangeValidator.class);
        }

        @Bean
        public RegistrarUsuarioUseCase registrarUsuarioUseCase() {
            return  Mockito.mock(RegistrarUsuarioUseCase.class);
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}