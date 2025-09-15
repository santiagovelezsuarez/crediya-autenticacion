package co.pragma.api;

import co.pragma.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.Map;
import static java.util.Map.entry;

@Component
public class ErrorCodeHttpMapper {

    private static final Map<ErrorCode, HttpStatus> MAPPINGS = Map.ofEntries(
            entry(ErrorCode.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT),
            entry(ErrorCode.SALARIO_OUT_OF_RANGE, HttpStatus.UNPROCESSABLE_ENTITY),
            entry(ErrorCode.USUARIO_NOT_FOUND, HttpStatus.NOT_FOUND),
            entry(ErrorCode.ROL_NOT_FOUND, HttpStatus.NOT_FOUND),
            entry(ErrorCode.DOCUMENTO_ALREADY_REGISTERED, HttpStatus.CONFLICT),
            entry(ErrorCode.CREDENCIALES_INVALIDAS, HttpStatus.UNAUTHORIZED),
            entry(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN),
            entry(ErrorCode.TECHNICAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
            entry(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST),
            entry(ErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST),
            entry(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
    );

    public HttpStatus toHttpStatus(ErrorCode code) {
        return MAPPINGS.getOrDefault(code, HttpStatus.BAD_REQUEST);
    }
}
