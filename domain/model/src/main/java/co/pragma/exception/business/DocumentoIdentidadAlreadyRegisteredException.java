package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class DocumentoIdentidadAlreadyRegisteredException extends BusinessException {
    public  DocumentoIdentidadAlreadyRegisteredException(String message) {
        super(ErrorCode.DOCUMENTO_ALREADY_REGISTERED, message);
    }
}
