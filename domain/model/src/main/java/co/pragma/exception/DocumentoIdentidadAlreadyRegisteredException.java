package co.pragma.exception;

public class DocumentoIdentidadAlreadyRegisteredException extends BusinessException {
    public  DocumentoIdentidadAlreadyRegisteredException(String message) {
        super(ErrorCode.DOCUMENTO_ALREADY_REGISTERED, message);
    }
}
