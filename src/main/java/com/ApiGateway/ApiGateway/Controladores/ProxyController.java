package com.ApiGateway.ApiGateway.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;

@RestController
public class ProxyController {

    @Autowired
    private LoadBalancerClient loadBalancer;

    private final RestTemplate restTemplate = new RestTemplate();

    // Proxy para UsuarioApi
    @RequestMapping(value = "/api/usuarios/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyUsuarios(HttpServletRequest request, @RequestBody(required = false) Object body) {
        return proxyRequest("usuario-microservice", request, body);
    }

    @RequestMapping(value = "/api/roles/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyRoles(HttpServletRequest request, @RequestBody(required = false) Object body) {
        return proxyRequest("usuario-microservice", request, body);
    }

    @RequestMapping(value = "/api/auth/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyAuth(HttpServletRequest request, @RequestBody(required = false) Object body) {
        return proxyRequest("usuario-microservice", request, body);
    }

    // Proxy para BlogApi
    @RequestMapping(value = "/api/blogs/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyBlogs(HttpServletRequest request, @RequestBody(required = false) Object body) {
        return proxyRequest("blog-service", request, body);
    }

    private ResponseEntity<?> proxyRequest(String serviceName, HttpServletRequest request, Object body) {
        try {
            // Obtener instancia del servicio
            ServiceInstance instance = loadBalancer.choose(serviceName);
            if (instance == null) {
                return ResponseEntity.status(503).body("Servicio " + serviceName + " no disponible");
            }

            // Construir URL del microservicio
            String targetUrl = instance.getUri().toString() + request.getRequestURI();
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }

            // Copiar headers del request original
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.set(headerName, headerValue);
            }

            // Crear entidad HTTP
            HttpEntity<Object> entity = new HttpEntity<>(body, headers);

            // Determinar método HTTP
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            // Realizar petición al microservicio
            ResponseEntity<Object> response = restTemplate.exchange(targetUrl, method, entity, Object.class);

            return response;

        } catch (Exception e) {
            System.err.println("Error en proxy para " + serviceName + ": " + e.getMessage());
            return ResponseEntity.status(500).body("Error interno del gateway: " + e.getMessage());
        }
    }
}