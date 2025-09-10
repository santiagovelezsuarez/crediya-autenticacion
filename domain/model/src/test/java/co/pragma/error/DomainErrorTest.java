package co.pragma.error;

import co.pragma.exception.business.BusinessException;
import co.pragma.exception.business.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DomainErrorTest {
    @Test
    void shouldMapBusinessExceptionToDomainError() {
        BusinessException ex = new EmailAlreadyRegisteredException();

        DomainError error = DomainError.from(ex);

        assertThat(error.code()).isEqualTo("EMAIL_ALREADY_REGISTERED");
        assertThat(error.message()).isEqualTo(ErrorCode.EMAIL_ALREADY_REGISTERED.getDefaultMessage());
    }

}
