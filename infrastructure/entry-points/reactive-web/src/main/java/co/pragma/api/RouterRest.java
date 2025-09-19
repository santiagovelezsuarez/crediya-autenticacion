package co.pragma.api;

import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.ErrorResponse;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.api.handler.UsuarioHandler;
import co.pragma.api.security.SecurityHandlerFilter;
import co.pragma.security.PermissionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final SecurityHandlerFilter securityFilter;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.POST,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenRegisterUser",
                    operation = @Operation(
                            operationId = "RegistrarUsuario",
                            summary = "Registrar un nuevo usuario",
                            description = "Crea un nuevo usuario en el sistema",
                            tags = {"Registrar Usuario"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RegistrarUsuarioDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente",
                                            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                                    @ApiResponse(responseCode = "409", description = "Email o documento ya registrado",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "422", description = "Salario fuera de rango",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Request inválido",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{tipoDocumento}/{numeroDocumento}",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    method = RequestMethod.GET,
                    beanClass = UsuarioHandler.class,
                    beanMethod = "listenFindByDocumento",
                    operation = @Operation(
                            operationId = "ObtenerUsuarioPorDocumento",
                            summary = "Obtener usuario por tipo y número de documento",
                            description = "Busca un usuario en el sistema usando su tipo y número de documento",
                            tags = {"Obtener Usuario"},
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> userRoutes(UsuarioHandler handler) {
        return route()
                .POST("/api/v1/usuarios", handler::listenRegisterUser)
                .filter(securityFilter.requirePermission(PermissionEnum.REGISTRAR_USUARIO))
                .build();
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
                            description = "Verifica que el servicio está disponible",
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
