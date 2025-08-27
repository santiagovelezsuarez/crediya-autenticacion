package co.pragma.base.exception;

public class EmailAlreadyRegisteredException extends BusinessException {
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
