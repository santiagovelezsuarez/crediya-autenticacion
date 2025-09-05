package co.pragma.api.handler.service;

import co.pragma.api.dto.AutenticarUsuarioDTO;
import co.pragma.api.dto.DtoValidatorBuilder;
import co.pragma.api.dto.RegistrarUsuarioDTO;
import co.pragma.api.dto.UsuarioDtoMapper;
import co.pragma.api.security.JwtService;
import co.pragma.api.security.SessionValidator;
import co.pragma.model.security.Permission;
import co.pragma.model.usuario.Usuario;
import co.pragma.model.usuario.gateways.PasswordEncoderService;
import co.pragma.model.usuario.gateways.SessionProvider;
import co.pragma.usecase.usuario.UsuarioUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioUseCase usuarioUseCase;
    private final SessionValidator sessionValidator;
    private final UsuarioDtoMapper usuarioDtoMapper;
    private final Validator validator;
    private final JwtService jwtService;
    private final SessionProvider sessionProvider;
    private final PasswordEncoderService passwordEncoderService;

    public Mono<Usuario> registerUser(RegistrarUsuarioDTO dto) {
        return processRegistrationDTO(dto);
    }

    public Mono<AuthResult> authenticate(AutenticarUsuarioDTO dto) {
        return DtoValidatorBuilder.validate(dto, validator)
                .flatMap(vdto -> usuarioUseCase.authenticate(vdto.getEmail(), vdto.getPassword()))
                .map(usuario -> new AuthResult(usuario, jwtService.generateToken(usuario)));
    }

    private Mono<Usuario> processRegistrationDTO(RegistrarUsuarioDTO dto) {
        return sessionProvider.getCurrentSession()
                .flatMap(session -> sessionValidator.validatePermission(session, Permission.REGISTRAR_USUARIO)
                        .then(DtoValidatorBuilder.validate(dto, validator))
                        .flatMap(this::hashPassword)
                        .flatMap(user -> usuarioUseCase.registerUser(user, session))
                );
    }

    private Mono<Usuario> hashPassword(RegistrarUsuarioDTO dto) {
        Usuario user = usuarioDtoMapper.toModel(dto);
        return passwordEncoderService.encodeReactive(dto.getPassword())
                .map(hash -> user.toBuilder()
                        .passwordHash(hash)
                        .build());
    }

    public record AuthResult(Usuario usuario, String token) {}
}
