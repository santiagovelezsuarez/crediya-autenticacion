package co.pragma.api;

import co.pragma.api.adapter.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.dto.request.RegistrarUsuarioDTO;
import co.pragma.api.dto.response.UsuarioResponse;
import co.pragma.api.handler.UsuarioHandler;
import co.pragma.api.mapper.UsuarioDtoMapper;
import co.pragma.api.security.SecurityHandlerFilter;
import co.pragma.api.security.UserContextExtractor;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.command.RegistrarUsuarioCommand;
import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import co.pragma.usecase.security.PermissionValidator;
import co.pragma.usecase.usuario.RegistrarUsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, UsuarioHandler.class})
@Import({SecurityHandlerFilter.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @MockitoBean
    private UsuarioDtoMapper usuarioDtoMapper;

    @MockitoBean
    private PermissionValidator permissionValidator;

    @MockitoBean
    private UserContextExtractor userContextExtractor;

    @MockitoBean
    private DtoValidator dtoValidator;

    @MockitoBean
    private ResponseService responseService;

    private RegistrarUsuarioDTO requestDTO;
    private UsuarioResponse responseDTO;
    private UserContextRequest userContext;

    @BeforeEach
    void setUp() {
        requestDTO = RegistrarUsuarioDTO.builder()
                .nombres("santi").apellidos("velez").tipoDocumento("CC").numeroDocumento("12345678")
                .fechaNacimiento("1988-10-07").direccion("Cl 88").telefono("3107888888")
                .email("santi@mail.co").password("12345678").salarioBase(BigDecimal.valueOf(2750000))
                .rol("CLIENTE").build();

        responseDTO = UsuarioResponse.builder()
                .id("28f279f3-1ad7-47a7-a7e4-d3c9473afdc1")
                .nombres("santi").apellidos("velez")
                .tipoDocumento("CC").numeroDocumento("12345678")
                .fechaNacimiento("1988-10-07")
                .direccion("Cl 88").telefono("3107888888").email("santi@mail.co")
                .salarioBase(BigDecimal.valueOf(2750000)).rol("CLIENTE").build();

        userContext = new UserContextRequest("admin-123", "admin@test.com", "ADMIN",
                Set.of(PermissionEnum.REGISTRAR_USUARIO));
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        Usuario usuario = Usuario.builder()
                .id(UUID.fromString(responseDTO.id()))
                .nombres(requestDTO.getNombres())
                .apellidos(requestDTO.getApellidos())
                .numeroDocumento(requestDTO.getNumeroDocumento())
                .email(requestDTO.getEmail())
                .build();

        when(userContextExtractor.fromRequest(any()))
                .thenReturn(userContext);
        when(permissionValidator.requirePermission(any(), eq(PermissionEnum.REGISTRAR_USUARIO)))
                .thenReturn(Mono.empty());
        when(dtoValidator.validate(any(RegistrarUsuarioDTO.class)))
                .thenReturn(Mono.just(requestDTO));
        when(usuarioDtoMapper.toCommand(any()))
                .thenReturn(RegistrarUsuarioCommand.builder().build());
        when(registrarUsuarioUseCase.execute(any()))
                .thenReturn(Mono.just(usuario));
        when(usuarioDtoMapper.toResponse(any()))
                .thenReturn(responseDTO);
        when(responseService.createdJson(any()))
                .thenAnswer(inv -> ServerResponse.status(201).bodyValue(inv.getArgument(0)));

        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(responseDTO.id())
                .jsonPath("$.email").isEqualTo(responseDTO.email())
                .jsonPath("$.nombres").isEqualTo(responseDTO.nombres());

        verify(userContextExtractor).fromRequest(any());
        verify(permissionValidator).requirePermission(any(), eq(PermissionEnum.REGISTRAR_USUARIO));
        verify(registrarUsuarioUseCase).execute(any());
    }
}
