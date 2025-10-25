package com.ApiGateway.ApiGateway.filters;

import com.ApiGateway.ApiGateway.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Permitir rutas públicas sin autenticación
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        // Verificar si hay token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        // Si no hay token y la ruta requiere autenticación
        if (jwt == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token de acceso requerido\"}");
            return;
        }

        // Validar el token
        try {
            if (!jwtUtil.validateToken(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
                return;
            }

            // Agregar headers con información del usuario para los microservicios
            String username = jwtUtil.extractUsername(jwt);
            Long userId = jwtUtil.extractUserId(jwt);
            List<String> roles = jwtUtil.extractRoles(jwt);
            String nombre = jwtUtil.extractNombre(jwt);

            // Crear un wrapper del request para agregar headers
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request);
            wrappedRequest.addHeader("X-User-Id", userId.toString());
            wrappedRequest.addHeader("X-User-Email", username);
            wrappedRequest.addHeader("X-User-Name", nombre);
            wrappedRequest.addHeader("X-User-Roles", String.join(",", roles));

            filterChain.doFilter(wrappedRequest, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Error procesando token: " + e.getMessage() + "\"}");
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/**") ||
                path.equals("/") ||
                path.startsWith("/debug/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    // Clase interna para wrapper del request
    private static class HttpServletRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        private final java.util.Map<String, String> customHeaders = new java.util.HashMap<>();

        public HttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void addHeader(String name, String value) {
            customHeaders.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = customHeaders.get(name);
            if (headerValue != null) {
                return headerValue;
            }
            return super.getHeader(name);
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            java.util.Set<String> set = new java.util.HashSet<>(customHeaders.keySet());
            java.util.Enumeration<String> e = super.getHeaderNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
            return java.util.Collections.enumeration(set);
        }
    }
}