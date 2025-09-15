package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class AuthenticationException extends BusinessException {
    public AuthenticationException() {
        super(ErrorCode.CREDENCIALES_INVALIDAS);
    }
}
