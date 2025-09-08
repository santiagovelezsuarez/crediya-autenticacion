package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(ErrorCode.CREDENCIALES_INVALIDAS, message);
    }
}
