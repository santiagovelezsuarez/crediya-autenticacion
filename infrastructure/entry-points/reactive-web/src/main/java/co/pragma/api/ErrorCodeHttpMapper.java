package co.pragma.api;

import co.pragma.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ErrorCodeHttpMapper {

    private static final Map<ErrorCode, HttpStatus> MAPPINGS = Map.of(
            ErrorCode.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT,
            ErrorCode.SALARIO_OUT_OF_RANGE, HttpStatus.UNPROCESSABLE_ENTITY,
            ErrorCode.USUARIO_NOT_FOUND, HttpStatus.NOT_FOUND,
            ErrorCode.DOCUMENTO_ALREADY_REGISTERED, HttpStatus.CONFLICT
    );

    public HttpStatus toHttpStatus(ErrorCode code) {
        return MAPPINGS.getOrDefault(code, HttpStatus.BAD_REQUEST);
    }
}
