package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class SalarioBaseException extends BusinessException {
    public SalarioBaseException(String message) {
        super(ErrorCode.SALARIO_OUT_OF_RANGE, message);
    }
}
