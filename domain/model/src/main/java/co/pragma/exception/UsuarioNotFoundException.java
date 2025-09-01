package co.pragma.exception;

public class UsuarioNotFoundException extends BusinessException {
    public UsuarioNotFoundException(String message) {
        super(ErrorCode.USUARIO_NOT_FOUND, message);
    }
}
