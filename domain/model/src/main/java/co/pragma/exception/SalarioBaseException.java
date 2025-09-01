package co.pragma.exception;

public class SalarioBaseException extends BusinessException {
    public SalarioBaseException(String message) {
        super(ErrorCode.SALARIO_OUT_OF_RANGE, message);
    }
}
