package co.pragma.exception;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(ErrorCode.CREDENCIALES_INVALIDAS, message);
    }
}
