package co.pragma.api.handler;

import co.pragma.api.adapters.ResponseService;
import co.pragma.api.dto.DtoValidator;
import co.pragma.api.mapper.UsuarioDtoMapper;
import co.pragma.api.dto.request.GetUsuariosBatchDTO;
import co.pragma.api.dto.response.UsuarioInfoListResponse;
import co.pragma.exception.business.UsuarioNotFoundException;
import co.pragma.model.session.Permission;
import co.pragma.model.session.PermissionValidator;
import co.pragma.model.usuario.gateways.UsuarioRepository;
import co.pragma.usecase.usuario.ConsultarUsuariosBatchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioQueryHandler {

    private final ConsultarUsuariosBatchUseCase consultarUsuariosBatchUseCase;
    private final DtoValidator dtoValidator;
    private final UsuarioRepository usuarioRepository;
    private final PermissionValidator permissionValidator;
    private final UsuarioDtoMapper usuarioDtoMapper;
    private final ResponseService responseService;

    public Mono<ServerResponse> listenGetUsuariosBatch(ServerRequest serverRequest) {
        log.debug("Petición recibida para consulta batch de usuarios");
        return serverRequest
                .bodyToMono(GetUsuariosBatchDTO.class)
                .flatMap(dtoValidator::validate)
                .flatMap(list -> permissionValidator.requirePermission(Permission.LISTAR_SOLICITUDES_PENDIENTES).thenReturn(list))
                .doOnNext(userIds -> log.trace("Consultando {} usuarios: {}", userIds.getUserIds().size(), userIds))
                .flatMapMany(dto -> consultarUsuariosBatchUseCase.execute(dto.getUserIds()))
                .collectList()
                .map(usuarioDtoMapper::toUsuarioInfoDtoList)
                .map(UsuarioInfoListResponse::new)
                .flatMap(responseService::okJson);
    }

    public Mono<ServerResponse> listenGetUsuarioById(ServerRequest serverRequest) {
        log.debug("Petición recibida para consulta de usuario por Id");
        String id = serverRequest.pathVariable("id");

        return permissionValidator.requirePermission(Permission.APROBAR_SOLICITUD)
                .then(usuarioRepository.findById(UUID.fromString(id)))
                .doOnNext(usuario -> log.trace("Usuario encontrado: {}", usuario))
                .map(usuarioDtoMapper::toUsuarioInfoDTO)
                .flatMap(responseService::okJson)
                .switchIfEmpty(Mono.error(new UsuarioNotFoundException()));

    }

}
