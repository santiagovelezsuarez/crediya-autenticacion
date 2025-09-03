package co.pragma.exception;

public class EmailAlreadyRegisteredException extends BusinessException {
    public EmailAlreadyRegisteredException(String message) {
        super(ErrorCode.EMAIL_ALREADY_REGISTERED, message);
    }
}
