package co.pragma.api;

import co.pragma.api.dto.DtoValidationException;
import co.pragma.api.dto.ErrorResponse;
import co.pragma.exception.EmailAlreadyRegisteredException;
import co.pragma.exception.SalarioBaseException;
import co.pragma.exception.UsuarioNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyRegistered_shouldReturnConflict() {
        var ex = new EmailAlreadyRegisteredException("El email ya está registrado");
        ServerHttpRequest request = MockServerHttpRequest.get("/usuarios").build();

        var result = handler.handle(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo("EMAIL_ALREADY_REGISTERED");
                    assertThat(body.getMessage()).isEqualTo("El email ya está registrado");
                })
                .verifyComplete();
    }

    @Test
    void handleSalarioOutOfRange_shouldReturnUnprocessableEntity() {
        var ex = new SalarioBaseException("El salario está fuera de rango");
        ServerHttpRequest request = MockServerHttpRequest.post("/usuarios").build();

        var result = handler.handle(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo("SALARIO_OUT_OF_RANGE");
                })
                .verifyComplete();
    }

    @Test
    void handleUsuarioNotFound_shouldReturnNotFound() {
        var ex = new UsuarioNotFoundException("Usuario no encontrado");
        ServerHttpRequest request = MockServerHttpRequest.get("/usuarios/123").build();

        var result = handler.handle(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo("USUARIO_NOT_FOUND");
                })
                .verifyComplete();
    }

    @Test
    void handleValidationException_shouldReturnBadRequestWithErrors() {
        var fieldErrors = List.of(
                new DtoValidationException.FieldError("email", "Email inválido")
        );
        var ex = new DtoValidationException(fieldErrors);
        ServerHttpRequest request = MockServerHttpRequest.post("/usuarios").build();

        var result = handler.handleValidationException(ex, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.getError()).isEqualTo("VALIDATION_ERROR");
                    assertThat(body.getValidationErrors()).hasSize(1);
                    assertThat(body.getValidationErrors().get(0).getField()).isEqualTo("email");
                })
                .verifyComplete();
    }

    @Test
    void handleGeneralException_shouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Error inesperado");

        var result = handler.handleGeneralException(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    ErrorResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getError()).isEqualTo("INTERNAL_SERVER_ERROR");
                })
                .verifyComplete();
    }

}
