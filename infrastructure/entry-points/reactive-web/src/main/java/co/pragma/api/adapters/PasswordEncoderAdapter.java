package co.pragma.api.adapters;

import co.pragma.model.usuario.gateways.PasswordEncoderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncoderService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean matches(CharSequence raw, String hash) {
        return passwordEncoder.matches(raw, hash);
    }

    @Override
    public Mono<String> encodeReactive(CharSequence raw) {
        return Mono.fromCallable(() -> passwordEncoder.encode(raw))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
