package co.pragma.common.exception;

public class EmailAlreadyRegisteredException extends  BusinessException {
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
