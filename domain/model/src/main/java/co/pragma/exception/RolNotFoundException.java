package co.pragma.exception;

public class RolNotFoundException extends BusinessException {
    public RolNotFoundException(String message) {
        super(ErrorCode.ROL_NOT_FOUND, message);
    }
}
