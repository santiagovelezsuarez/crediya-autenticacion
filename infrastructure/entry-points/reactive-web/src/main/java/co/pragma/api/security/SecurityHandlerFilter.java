package co.pragma.api.security;

import co.pragma.security.PermissionEnum;
import co.pragma.security.UserContextRequest;
import co.pragma.usecase.security.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityHandlerFilter {

    private final PermissionValidator permissionValidator;
    private final UserContextExtractor userContextExtractor;

    public HandlerFilterFunction<ServerResponse, ServerResponse> requirePermission(PermissionEnum permissionEnum) {
        return (request, next) -> {
            UserContextRequest context = userContextExtractor.fromRequest(request);
            return permissionValidator.requirePermission(context, permissionEnum)
                    .then(Mono.defer(() -> {
                        request.attributes().put("userContext", context);
                        return next.handle(request);
                    }));

        };
    }
}
