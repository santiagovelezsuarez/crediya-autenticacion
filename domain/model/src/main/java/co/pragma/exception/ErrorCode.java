package co.pragma.exception;

public enum ErrorCode {
    EMAIL_ALREADY_REGISTERED("El correo electrónico ya está registrado."),
    DOCUMENTO_ALREADY_REGISTERED("El número de documento ya está registrado."),
    SALARIO_OUT_OF_RANGE("El salario base no es válido."),
    USUARIO_NOT_FOUND("Usuario no encontrado."),
    CREDENCIALES_INVALIDAS("Usuario o contraseña inválidos."),
    ROL_NOT_FOUND("El rol especificado no existe."),
    UNAUTHORIZED("No se encontró una sesión autenticada"),
    FORBIDDEN("No tiene permisos para realizar esta acción."),
    TECHNICAL_ERROR("Ocurrió un error técnico, intente más tarde."),
    INVALID_INPUT("Existen errores de validación."),
    INVALID_REQUEST("La solicitud es inválida o malformada."),
    INTERNAL_SERVER_ERROR("Ocurrió un error interno, por favor intente más tarde."),
    DB_ERROR("Error de base de datos");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
