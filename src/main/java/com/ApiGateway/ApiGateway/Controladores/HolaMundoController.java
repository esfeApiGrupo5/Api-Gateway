package com.ApiGateway.ApiGateway.Controladores;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaMundoController {
    @GetMapping("/")
    public String holaMundo() {
        return "¡Hola Mundo desde el controlador del Api Gateway! esto es el get";
    }


    @PostMapping("/")
    public String holaMundo2() {
        return "¡Hola Mundo desde Spring Boot! post";
    }
    @DeleteMapping("/")
    public String holaMundo3() {
        return "¡Hola Mundo desde Spring Boot! delete";
    }
    @PutMapping("/")
    public String holaMundo4() {
        return "¡Hola Mundo desde Spring Boot! put";
    }

}
