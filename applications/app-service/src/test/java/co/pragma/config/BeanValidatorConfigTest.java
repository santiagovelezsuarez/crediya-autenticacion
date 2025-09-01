package co.pragma.config;

import co.pragma.usecase.usuario.businessrules.SalarioRangeValidator;
import co.pragma.usecase.usuario.businessrules.UniqueEmailValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeanValidatorConfigTest {

    @Autowired
    private UniqueEmailValidator uniqueEmailValidator;

    @Autowired
    private SalarioRangeValidator salarioRangeValidator;

    @Test
    void contextLoads() {
        assertNotNull(uniqueEmailValidator);
        assertNotNull(salarioRangeValidator);
    }
}

