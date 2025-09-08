package co.pragma.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    /** Business error messages
     */
    public static final String INVALID_CREDENTIALS = "Usuario o contraseña inválidos.";
    public static final String EMAIL_ALREADY_EXISTS = "El correo electrónico ya está registrado.";
    public static final String DOCUMENT_ALREADY_EXISTS = "El número de documento ya está registrado.";
    public static final String SALARY_OUT_OF_RANGE_MAX = "El salario base no puede ser mayor a ";
    public static final String SALARY_OUT_OF_RANGE_MIN = "El salario base debe ser mayor a 0.";
    public static final String USER_NOT_FOUND = "Usuario no encontrado.";
    public static final String ROLE_NOT_FOUND = "El rol especificado no existe.";
    public static final String FORBIDDEN = "No tiene permisos para realizar esta acción.";

    /** System error messages
     */
    public static final String GENERIC_INTERNAL = "Ocurrió un error interno, por favor intente más tarde.";
    public static final String INVALID_REQUEST = "La solicitud es inválida o malformada.";
    public static final String INVALID_INPUT = "Existen errores de validación.";


}
