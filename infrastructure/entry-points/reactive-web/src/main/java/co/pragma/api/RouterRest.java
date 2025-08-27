package co.pragma.api;

import co.pragma.api.dto.UsuarioRequest;
import co.pragma.api.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations(
            @RouterOperation(
                    path = "/api/v1/user",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveTask",
                    operation = @Operation(
                            operationId = "saveUser",
                            summary = "Registrar un nuevo usuario",
                            description = "Crea un usuario en el sistema",
                            tags = {"Usuario"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UsuarioRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Usuario registrado correctamente",
                                            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Request inválido"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    )
    public RouterFunction<ServerResponse> userRoutes(Handler handler) {
        return route(POST("/api/v1/user"), handler::listenSaveTask);
    }

    @Bean
    @RouterOperations(
            @RouterOperation(
                    path = "/api/health",
                    produces = {MediaType.TEXT_PLAIN_VALUE},
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "healthCheck",
                            summary = "Health check",
                            description = "Verifica que el servicio está arriba",
                            tags = {"Health"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Servicio disponible")
                            }
                    )
            )
    )
    public RouterFunction<ServerResponse> healthRoutes() {
        return route(GET("/api/health"),
                request -> ServerResponse.ok().bodyValue("OK"));
    }
}
