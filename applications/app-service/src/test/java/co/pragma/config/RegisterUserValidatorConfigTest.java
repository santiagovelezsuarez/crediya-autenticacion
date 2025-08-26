package co.pragma.config;

import co.pragma.common.gateways.BusinessValidator;
import co.pragma.model.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RegisterUserValidatorConfigTest {

    @Autowired
    private BusinessValidator<Usuario> registrationValidator;

    @Test
    void contextLoads() {
        assertNotNull(registrationValidator);
    }
}

