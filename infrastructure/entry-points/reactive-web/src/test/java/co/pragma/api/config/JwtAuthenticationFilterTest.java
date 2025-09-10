package co.pragma.api.config;

import co.pragma.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtService jwtService;

    @Mock
    WebFilterChain chain;

    @Mock
    ServerWebExchange exchange;

    @Mock
    ServerHttpRequest request;

    @Mock
    ServerHttpResponse response;

    JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter(jwtService);
        when(exchange.getRequest()).thenReturn(request);
    }

    @Test
    void shouldContinueFilterIfNoAuthorizationHeader() {
        when(request.getHeaders()).thenReturn(HttpHeaders.EMPTY);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }

    @Test
    void shouldSetUnauthorizedForInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalidtoken");
        when(request.getHeaders()).thenReturn(headers);
        when(jwtService.getUsernameFromToken("invalidtoken")).thenThrow(new RuntimeException());
        when(exchange.getResponse()).thenReturn(response);
        when(response.setComplete()).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response).setComplete();
        verifyNoInteractions(chain);
    }

    @Test
    void shouldAuthenticateValidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer validtoken");
        when(request.getHeaders()).thenReturn(headers);
        when(jwtService.getUsernameFromToken("validtoken")).thenReturn("user");
        when(jwtService.getRoleFromToken("validtoken")).thenReturn("ADMIN");
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }
}
