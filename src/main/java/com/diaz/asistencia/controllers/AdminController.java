package com.diaz.asistencia.controllers;

import com.diaz.asistencia.entities.Almacen;
import com.diaz.asistencia.entities.Asistencia;
import com.diaz.asistencia.entities.GastoExtra;
import com.diaz.asistencia.entities.Usuario;
import com.diaz.asistencia.repositories.AlmacenRepository;
import com.diaz.asistencia.repositories.AsistenciaRepository;
import com.diaz.asistencia.repositories.GastoExtraRepository;
import com.diaz.asistencia.repositories.UsuarioRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository    usuarioRepository;
    private final AlmacenRepository    almacenRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final GastoExtraRepository gastoExtraRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminController(
            UsuarioRepository usuarioRepository,
            AlmacenRepository almacenRepository,
            AsistenciaRepository asistenciaRepository,
            GastoExtraRepository gastoExtraRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository    = usuarioRepository;
        this.almacenRepository    = almacenRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.gastoExtraRepository = gastoExtraRepository;
        this.passwordEncoder      = passwordEncoder;
    }

    // ═══════════════ USUARIOS ═══════════════════════════════════════════════

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findByActivoTrue());
    }

    @GetMapping("/usuarios/inactivos")
    public ResponseEntity<List<Usuario>> listarUsuariosInactivos() {
        return ResponseEntity.ok(usuarioRepository.findByActivoFalse());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            if (usuario.getUsuario() == null || usuario.getUsuario().isBlank())
                return ResponseEntity.badRequest().body("El nombre de usuario es obligatorio");
            if (usuario.getPassword() == null || usuario.getPassword().isBlank())
                return ResponseEntity.badRequest().body("La contraseña es obligatoria");
            if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
                return ResponseEntity.badRequest().body("Ya existe un usuario con ese nombre");

            usuario.setActivo(true);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return ResponseEntity.ok(usuarioRepository.save(usuario));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear usuario: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario desactivado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reactivar/usuario/{id}")
    public ResponseEntity<String> reactivarUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario reactivado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Historial de asistencias de un trabajador específico
    @GetMapping("/usuarios/{id}/asistencias")
    public ResponseEntity<?> asistenciasPorUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return ResponseEntity.ok(asistenciaRepository.findByUsuario(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Pagos extra de un trabajador con motivo — para que el admin los vea en detalle
    @GetMapping("/usuarios/{id}/pagos-extra")
    public ResponseEntity<?> pagosExtraPorUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            return ResponseEntity.ok(gastoExtraRepository.findByUsuario(usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ═══════════════ ALMACENES ══════════════════════════════════════════════

    @GetMapping("/almacenes")
    public ResponseEntity<List<Almacen>> listarAlmacenes() {
        return ResponseEntity.ok(almacenRepository.findByEstadoTrue());
    }

    @GetMapping("/almacenes/inactivos")
    public ResponseEntity<List<Almacen>> listarAlmacenesInactivos() {
        return ResponseEntity.ok(almacenRepository.findByEstadoFalse());
    }

    @PostMapping("/almacenes")
    public ResponseEntity<?> crearAlmacen(@RequestBody Almacen almacen) {
        try {
            if (almacen.getNombre() == null || almacen.getNombre().isBlank())
                return ResponseEntity.badRequest().body("El nombre del almacén es obligatorio");
            if (almacen.getPagoBase() == null || almacen.getPagoBase() <= 0)
                return ResponseEntity.badRequest().body("El pago base debe ser mayor a cero");

            almacen.setEstado(true);
            return ResponseEntity.ok(almacenRepository.save(almacen));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear almacén: " + e.getMessage());
        }
    }

    @DeleteMapping("/almacenes/{id}")
    public ResponseEntity<String> eliminarAlmacen(@PathVariable Long id) {
        try {
            Almacen almacen = almacenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
            almacen.setEstado(false);
            almacenRepository.save(almacen);
            return ResponseEntity.ok("Almacén desactivado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reactivar/almacen/{id}")
    public ResponseEntity<String> reactivarAlmacen(@PathVariable Long id) {
        try {
            Almacen almacen = almacenRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));
            almacen.setEstado(true);
            almacenRepository.save(almacen);
            return ResponseEntity.ok("Almacén reactivado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ═══════════════ PAGOS EXTRA (ADMIN) ════════════════════════════════════

    @GetMapping("/pagos-extra")
    public ResponseEntity<List<GastoExtra>> listarPagosExtra() {
        return ResponseEntity.ok(gastoExtraRepository.findAll());
    }

    @PostMapping("/pagos-extra")
    public ResponseEntity<?> crearPagoExtra(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            String motivo  = body.get("motivo").toString();
            Double monto   = Double.valueOf(body.get("monto").toString());

            if (motivo.isBlank()) return ResponseEntity.badRequest().body("El motivo es obligatorio");
            if (monto <= 0)       return ResponseEntity.badRequest().body("El monto debe ser mayor a cero");

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GastoExtra gasto = new GastoExtra();
            gasto.setUsuario(usuario);
            gasto.setMotivo(motivo);
            gasto.setMonto(monto);
            gasto.setFecha(java.time.LocalDate.now());
            return ResponseEntity.ok(gastoExtraRepository.save(gasto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/pagos-extra/{id}")
    public ResponseEntity<String> eliminarPagoExtra(@PathVariable Long id) {
        try {
            if (!gastoExtraRepository.existsById(id))
                return ResponseEntity.badRequest().body("Pago extra no encontrado");
            gastoExtraRepository.deleteById(id);
            return ResponseEntity.ok("Pago extra eliminado");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}