package com.diaz.asistencia.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "API funcionando 🔥";
    }
}