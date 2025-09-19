package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class DocumentoIdentidadAlreadyRegisteredException extends BusinessException {
    public DocumentoIdentidadAlreadyRegisteredException() {
        super(ErrorCode.DOCUMENTO_ALREADY_REGISTERED);
    }
}
