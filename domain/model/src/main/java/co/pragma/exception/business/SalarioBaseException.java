package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class SalarioBaseException extends BusinessException {
    public SalarioBaseException(ErrorCode code) {
        super(code);
    }

    public SalarioBaseException(ErrorCode code, String customMessage) {
        super(code, customMessage);
    }
}
