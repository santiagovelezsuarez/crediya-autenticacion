package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class UsuarioNotFoundException extends BusinessException {
    public UsuarioNotFoundException(String message) {
        super(ErrorCode.USUARIO_NOT_FOUND, message);
    }
}
