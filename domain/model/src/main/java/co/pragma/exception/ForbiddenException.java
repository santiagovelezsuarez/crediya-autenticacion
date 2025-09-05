package co.pragma.exception;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
