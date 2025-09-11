package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.dto.request.GetUsuariosBatchDTO;
import co.pragma.api.dto.response.UsuarioInfoListResponse;
import co.pragma.model.rol.Permission;
import co.pragma.model.usuario.PermissionValidator;
import co.pragma.usecase.usuario.ConsultarUsuariosBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioQueryHandler {

    private final ConsultarUsuariosBatchUseCase consultarUsuariosBatchUseCase;
    private final PermissionValidator permissionValidator;
    private final UsuarioDtoMapper usuarioDtoMapper;
    private final ResponseService responseService;

    public Mono<ServerResponse> listenGetUsuariosBatch(ServerRequest serverRequest) {
        log.debug("Petición recibida para consulta batch de usuarios");
        return serverRequest
                .bodyToMono(GetUsuariosBatchDTO.class)
                .flatMap(list -> permissionValidator.requirePermission(Permission.LISTAR_SOLICITUDES_PENDIENTES).thenReturn(list))
                .doOnNext(userIds -> log.trace("Consultando {} usuarios: {}", userIds.getUserIds().size(), userIds))
                .flatMapMany(dto -> consultarUsuariosBatchUseCase.execute(dto.getUserIds()))
                .collectList()
                .map(usuarioDtoMapper::toUsuarioInfoDtoList)
                .map(UsuarioInfoListResponse::new)
                .flatMap(responseService::okJson);
    }
}
