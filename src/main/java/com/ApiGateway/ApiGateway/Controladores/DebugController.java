package com.ApiGateway.ApiGateway.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/debug/services")
    public Map<String, Object> getServices() {
        Map<String, Object> result = new HashMap<>();

        // Obtener todos los servicios registrados
        List<String> services = discoveryClient.getServices();
        result.put("services", services);

        // Obtener instancias de cada servicio
        Map<String, List<ServiceInstance>> serviceInstances = new HashMap<>();
        for (String service : services) {
            serviceInstances.put(service, discoveryClient.getInstances(service));
        }
        result.put("serviceInstances", serviceInstances);

        return result;
    }

    @GetMapping("/debug/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "API Gateway está funcionando");
        info.put("timestamp", java.time.LocalDateTime.now().toString());
        return info;
    }
}