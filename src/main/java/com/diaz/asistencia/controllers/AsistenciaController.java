package com.diaz.asistencia.controllers;

import com.diaz.asistencia.entities.Asistencia;
import com.diaz.asistencia.entities.GastoExtra;
import com.diaz.asistencia.services.AsistenciaService;
import com.diaz.asistencia.services.GastoExtraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final GastoExtraService gastoExtraService;

    public AsistenciaController(AsistenciaService asistenciaService, GastoExtraService gastoExtraService) {
        this.asistenciaService = asistenciaService;
        this.gastoExtraService = gastoExtraService;
    }

    @PostMapping("/almacen")
    public ResponseEntity<String> registrar(Authentication auth, @RequestParam Long almacenId) {
        try {
            asistenciaService.registrarAsistencia(auth.getName(), almacenId);
            return ResponseEntity.ok("Asistencia registrada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pago-extra")
    public ResponseEntity<String> registrarPagoExtra(Authentication auth, @RequestBody Map<String, Object> body) {
        try {
            String motivo = body.get("motivo").toString();
            Double monto  = Double.valueOf(body.get("monto").toString());
            gastoExtraService.registrar(auth.getName(), motivo, monto);
            return ResponseEntity.ok("Pago extra registrado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis")
    public ResponseEntity<List<Asistencia>> misAsistencias(Authentication auth) {
        try {
            return ResponseEntity.ok(asistenciaService.obtenerPorUsuario(auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Retorna los pagos extra del estibador con motivo y monto
    @GetMapping("/mis-pagos-extra")
    public ResponseEntity<List<GastoExtra>> misPagosExtra(Authentication auth) {
        try {
            return ResponseEntity.ok(asistenciaService.obtenerPagosExtraPorUsuario(auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mi-total")
    public ResponseEntity<Double> miTotal(Authentication auth) {
        try {
            return ResponseEntity.ok(asistenciaService.calcularTotalPorUsuario(auth.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}