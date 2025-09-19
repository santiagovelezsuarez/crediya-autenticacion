package co.pragma.api.config;

import co.pragma.usecase.security.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.reactive.CorsWebFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        var config = new org.springframework.web.cors.CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080/swagger-ui");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        var source = new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new org.springframework.web.cors.reactive.CorsWebFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PermissionValidator permissionValidator() {
        return new PermissionValidator();
    }
}