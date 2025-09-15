package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class UsuarioNotFoundException extends BusinessException {
    public UsuarioNotFoundException() {
        super(ErrorCode.USUARIO_NOT_FOUND);
    }
}
