package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void testBusinessExceptionWithCorrectErrorCode() {
        AuthenticationException exception = new AuthenticationException();

        assertNotNull(exception);
        assertEquals(ErrorCode.CREDENCIALES_INVALIDAS, exception.getCode());
        assertEquals(ErrorCode.CREDENCIALES_INVALIDAS.getDefaultMessage(), exception.getMessage());
    }

    @Test
    void testThrowableAndCatchable() {
        try {
            throw new AuthenticationException();
        } catch (BusinessException e) {
            assertInstanceOf(AuthenticationException.class, e, "La excepción debe ser AuthenticationException.");
        } catch (Exception e) {
            fail("Excepción incorrecta: " + e.getClass().getName());
        }
    }
}
