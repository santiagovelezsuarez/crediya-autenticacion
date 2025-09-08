package co.pragma.api.exception;

import co.pragma.api.ErrorCodeHttpMapper;
import co.pragma.api.dto.DtoValidationException;
import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.exception.business.ForbiddenException;
import co.pragma.exception.business.SalarioBaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.autoconfigure.cloudfoundry.CloudFoundryAuthorizationException;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class GlobalExceptionHandlerUnitTest {

    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private ErrorCodeHttpMapper errorCodeHttpMapper;

    @Mock
    private ServerCodecConfigurer codecConfigurer;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ServerRequest request;

    @Mock
    private ServerResponse response;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        when(codecConfigurer.getReaders()).thenReturn(List.of());
        when(codecConfigurer.getWriters()).thenReturn(List.of());
        when(applicationContext.getClassLoader()).thenReturn(getClass().getClassLoader());
        handler = new GlobalExceptionHandler(errorAttributes, applicationContext, codecConfigurer, errorCodeHttpMapper);
    }

    @Test
    void shouldHandleSalarioOutOfRange() {
        var ex = new SalarioBaseException(ErrorCode.SALARIO_OUT_OF_RANGE);
        var serverRequest = Mockito.mock(ServerRequest.class);

        when(errorAttributes.getError(serverRequest)).thenReturn(ex);
        when(errorCodeHttpMapper.toHttpStatus(ErrorCode.SALARIO_OUT_OF_RANGE)).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);

        Mono<ServerResponse> responseMono = Objects.requireNonNull(handler.getRoutingFunction(errorAttributes)
                        .route(serverRequest)
                        .block())
                .handle(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode())
                        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY))
                .verifyComplete();
    }

    @Test
    void shouldHandleValidationException() {
        DtoValidationException.FieldError fieldError = new DtoValidationException.FieldError("email", "El campo email debe ser una dirección de correo electrónico válida");
        List<DtoValidationException.FieldError> errors = List.of(fieldError);
        var ex = new DtoValidationException(errors);
        var serverRequest = Mockito.mock(ServerRequest.class);

        when(errorAttributes.getError(serverRequest)).thenReturn(ex);

        Mono<ServerResponse> responseMono = Objects.requireNonNull(handler.getRoutingFunction(errorAttributes)
                        .route(serverRequest)
                        .block())
                .handle(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
                .verifyComplete();
    }

    @Test
    void shouldHandleInfrastructureException() {
        var ex = new InfrastructureException("DB_ERROR", new RuntimeException("Connection failed"));
        var serverRequest = Mockito.mock(ServerRequest.class);

        when(errorAttributes.getError(serverRequest)).thenReturn(ex);

        Mono<ServerResponse> responseMono = Objects.requireNonNull(handler.getRoutingFunction(errorAttributes)
                        .route(serverRequest)
                        .block())
                .handle(serverRequest);

        StepVerifier.create(responseMono)
                .assertNext(response -> assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void shouldLogBusinessException(CapturedOutput output) {
        var ex = new ForbiddenException();

        when(request.path()).thenReturn("/error");
        when(request.method()).thenReturn(HttpMethod.GET);

        handler.logError(request, response, ex);

        assertTrue(output.getOut().contains("BusinessException at GET /error"));
    }

    @Test
    void shouldLogInfrastructureException(CapturedOutput output) {
        var ex = new InfrastructureException("DB_ERROR", new RuntimeException("Connection failed"));

        when(request.path()).thenReturn("/error");
        when(request.method()).thenReturn(HttpMethod.GET);

        handler.logError(request, response, ex);

        assertTrue(output.getOut().contains("InfrastructureException at GET /error:"));
    }

    @Test
    void shouldLogDtoValidationException(CapturedOutput output) {
        DtoValidationException.FieldError fieldError = new DtoValidationException.FieldError("email", "El campo email debe ser una dirección de correo electrónico válida");
        List<DtoValidationException.FieldError> errors = List.of(fieldError);
        var ex = new DtoValidationException(errors);

        when(request.path()).thenReturn("/error");
        when(request.method()).thenReturn(HttpMethod.POST);

        handler.logError(request, response, ex);

        assertTrue(output.getOut().contains("Validation error at POST /error:"));
    }

    @Test
    void shouldLogServerWebInputException(CapturedOutput output) {
        var ex = new ServerWebInputException(ErrorCode.INVALID_REQUEST.name());

        when(request.path()).thenReturn("/error");
        when(request.method()).thenReturn(HttpMethod.POST);

        handler.logError(request, response, ex);

        assertTrue(output.getOut().contains("Web input error at POST /error:"));
    }
}
