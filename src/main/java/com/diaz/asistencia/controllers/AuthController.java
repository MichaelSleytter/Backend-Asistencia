package com.diaz.asistencia.controllers;

import com.diaz.asistencia.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        try {

            // ✅ Validación segura
            String usuario = request.get("usuario");
            String password = request.get("password");

            if (usuario == null || usuario.isBlank()
                    || password == null || password.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Faltan datos"));
            }

            // ✅ llamada al servicio
            Map<String, String> response = authService.login(usuario, password);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            // ✅ error controlado (credenciales)
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {

            // ✅ error inesperado
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error interno"));
        }
    }
}
