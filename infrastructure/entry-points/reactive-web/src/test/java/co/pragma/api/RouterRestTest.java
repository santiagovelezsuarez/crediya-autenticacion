package co.pragma.api;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.api.handler.AuthHandler;
import co.pragma.api.handler.UsuarioHandler;
import co.pragma.api.handler.UsuarioQueryHandler;
import co.pragma.api.mapper.UsuarioDtoMapper;
import co.pragma.model.session.PermissionValidator;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.Objects;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@WebFluxTest
(
        controllers = RouterRest.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration.class
})
@ContextConfiguration(classes = {RouterRest.class, UsuarioHandler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UsuarioHandler usuarioHandler;

    @MockitoBean
    private AuthHandler authHandler;

    @MockitoBean
    private UsuarioQueryHandler usuarioQueryHandler;

    @MockitoBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @MockitoBean
    private DtoValidator dtoValidator;

    @MockitoBean
    private ResponseService responseService;

    @MockitoBean
    private UsuarioDtoMapper usuarioDtoMapper;

    @MockitoBean
    private PermissionValidator permissionValidator;

    @Test
    void shouldRegisterNewUser() {
        var registrarUsuarioDTO = RegistrarUsuarioDTO.builder()
                .nombres("santi")
                .apellidos("velez")
                .tipoDocumento("CC")
                .numeroDocumento("12345")
                .fechaNacimiento("1988-10-07")
                .direccion("Cl 88")
                .telefono("3107888888")
                .email("santi@mail.co")
                .password("12345678")
                .salarioBase(BigDecimal.valueOf(2750000))
                .rol("CLIENTE")
                .build();
        var usuarioResponse = UsuarioResponse.builder()
                .id("28f279f3-1ad7-47a7-a7e4-d3c9473afdc1")
                .nombres("santi")
                .apellidos("velez")
                .tipoDocumento("CC")
                .numeroDocumento("12345")
                .fechaNacimiento("1988-10-07")
                .direccion("Cl 88")
                .telefono("3107888888")
                .email("santi@mail.co")
                .salarioBase(BigDecimal.valueOf(2750000))
                .rol("CLIENTE")
                .build();

        when(usuarioHandler.listenRegisterUser(org.mockito.ArgumentMatchers.any()))
                .thenReturn(Mono.just(Objects.requireNonNull(ServerResponse.status(HttpStatus.CREATED).bodyValue(usuarioResponse).block())));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(registrarUsuarioDTO))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UsuarioResponse.class);
    }

    @Test
    @DisplayName("should return OK on health check")
    void shouldReturnOkOnHealthCheck() {
        webTestClient.get()
                .uri("/api/health")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("OK");
    }

}