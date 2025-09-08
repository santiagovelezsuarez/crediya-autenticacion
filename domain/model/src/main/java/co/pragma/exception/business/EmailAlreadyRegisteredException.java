package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class EmailAlreadyRegisteredException extends BusinessException {
    public EmailAlreadyRegisteredException(String message) {
        super(ErrorCode.EMAIL_ALREADY_REGISTERED, message);
    }
}
