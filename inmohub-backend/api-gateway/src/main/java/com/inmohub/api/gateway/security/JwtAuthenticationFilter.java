package com.inmohub.api.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    private final List<String> openApiEndpoints = List.of(
            "api/v1/users/login",
            "api/v1/users/create",
            "/eureka"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Se omiten validaciones para rutas públicas
        if (isSecured(request)) {
            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Cabecera de autorización no encontrada.");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Formato de token inválido.");
            }

            String token = authHeader.substring(7);

            // Validación de Token
            if (!jwtUtils.isTokenValid(token)) {
                return onError(exchange, "Token inválido o expirado.");
            }

            // Extración de Claims e inyección de headers
            String userId = jwtUtils.getUserId(token);
            String roles = jwtUtils.getRoles(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }

    private boolean isSecured(ServerHttpRequest request) {
        return openApiEndpoints.stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Declaración de prioridad alta, ejecutar antes de enrutamiento
    }
}
