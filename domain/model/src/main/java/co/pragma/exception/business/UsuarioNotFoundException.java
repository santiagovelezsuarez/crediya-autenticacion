package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class UsuarioNotFoundException extends BusinessException {
    public UsuarioNotFoundException() {
        super(ErrorCode.USUARIO_NOT_FOUND);
    }
}
