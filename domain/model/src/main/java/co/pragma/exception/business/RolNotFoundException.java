package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class RolNotFoundException extends BusinessException {
    public RolNotFoundException(String message) {
        super(ErrorCode.ROL_NOT_FOUND, message);
    }
}
