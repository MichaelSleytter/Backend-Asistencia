package com.diaz.asistencia.controllers;

import com.diaz.asistencia.entities.Notificacion;
import com.diaz.asistencia.services.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // ─── ADMIN ───────────────────────────────────────────

    // Marcar a un trabajador como completamente pagado
    @PostMapping("/admin/usuarios/{id}/pago-completo")
    public ResponseEntity<String> pagoCompleto(@PathVariable Long id) {
        try {
            pagoService.marcarPagoCompleto(id);
            return ResponseEntity.ok("Pago registrado y notificación enviada al trabajador");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Registrar un adelanto para un trabajador
    @PostMapping("/admin/usuarios/{id}/adelanto")
    public ResponseEntity<String> registrarAdelanto(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        try {
            Double monto = Double.valueOf(body.get("monto").toString());
            pagoService.registrarAdelanto(id, monto);
            return ResponseEntity.ok("Adelanto registrado y notificación enviada al trabajador");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ─── ESTIBADOR ───────────────────────────────────────

    // Ver notificaciones pendientes del estibador
    @GetMapping("/asistencias/notificaciones")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(Authentication auth) {
        try {
            List<Notificacion> pendientes = pagoService.obtenerPendientes(auth.getName());
            return ResponseEntity.ok(pendientes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Confirmar una notificación (el estibador presiona "Confirmar")
    @PostMapping("/asistencias/notificaciones/{id}/confirmar")
    public ResponseEntity<String> confirmar(
            @PathVariable Long id,
            Authentication auth
    ) {
        try {
            pagoService.confirmarNotificacion(id, auth.getName());
            return ResponseEntity.ok("Confirmado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}