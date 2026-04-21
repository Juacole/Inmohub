package com.inmohub.lead.service.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro interno que confía en la validación previa del API Gateway.
 * Lee las cabeceras inyectadas y construye el contexto de seguridad local.
 * Este filtró siempre se ejecuta antes de que la petición llegué al controller.
 */
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String rolesHeader = request.getHeader("X-User-Role");

        // El gateway inyecta las cabeceras, se asume que la petición es legítima y esta autenticada
        if (userId != null && rolesHeader != null) {

            // Convertimos la cadena de roles (ROLE_ADMIN, ROLE_USER, etc.) en el formato que entiende Spring Security
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // Creación del token de autenticación, el userId como "Principal" y null en la contraseña
            // La contraseña a null porque el token ya esta autenticado
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    authorities
            );

            // Se guarda la autenticación en el contexto de Spring Security para cada petición
            // De esta forma se pueden acceder a las rutas protegidas
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continuamos con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}