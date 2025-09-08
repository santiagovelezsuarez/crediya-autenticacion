package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
