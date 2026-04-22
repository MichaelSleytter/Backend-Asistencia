package com.diaz.asistencia.services;

import com.diaz.asistencia.entities.*;
import com.diaz.asistencia.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final UsuarioRepository usuarioRepo;
    private final AsistenciaRepository asistenciaRepo;
    private final GastoExtraRepository gastoExtraRepo;
    private final NotificacionRepository notificacionRepo;

    public PagoService(
            UsuarioRepository usuarioRepo,
            AsistenciaRepository asistenciaRepo,
            GastoExtraRepository gastoExtraRepo,
            NotificacionRepository notificacionRepo
    ) {
        this.usuarioRepo = usuarioRepo;
        this.asistenciaRepo = asistenciaRepo;
        this.gastoExtraRepo = gastoExtraRepo;
        this.notificacionRepo = notificacionRepo;
    }

    /**
     * El admin marca al trabajador como PAGADO completamente.
     * Todas sus asistencias en estado REGISTRADO o VALIDADO pasan a PAGADO.
     * Se incluye la suma de los pagos extras (gastos extra) en el total.
     * Se crea una notificación de tipo PAGO_COMPLETO para que el estibador confirme.
     */
    @Transactional
    public void marcarPagoCompleto(Long usuarioId) {
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Asistencia> asistencias = asistenciaRepo.findByUsuario(usuario);
        List<GastoExtra> gastosExtra = gastoExtraRepo.findByUsuario(usuario);

        // Calcular total de días trabajados (asistencias pendientes de pago)
        double totalDias = asistenciaRepo.findByUsuario(usuario).stream()
                .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                .mapToDouble(Asistencia::getPagoDia)
                .sum();

        // Calcular total de pagos extras
        double totalGastosExtra = gastosExtra.stream()
                .mapToDouble(GastoExtra::getMonto)
                .sum();

        // Total = días trabajados + pagos extras
        double totalPagado = totalDias + totalGastosExtra;

        // Marcar todas las asistencias pendientes como PAGADO
        asistenciaRepo.findByUsuario(usuario).stream()
                .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                .forEach(a -> {
                    a.setEstado(EstadoAsistencia.PAGADO);
                    asistenciaRepo.save(a);
                });

        // Eliminar todos los gastos extras del usuario (ya pagados)
        gastoExtraRepo.deleteAll(gastosExtra);

        // Crear notificación para el estibador con el detalle
        String mensaje = String.format(
                "El administrador ha registrado el pago completo de tu sueldo.%n" +
                "Días trabajados: S/. %.2f%n" +
                "Pagos extras: S/. %.2f%n" +
                "Total: S/. %.2f",
                totalDias, totalGastosExtra, totalPagado
        );

        Notificacion notif = Notificacion.builder()
                .usuario(usuario)
                .tipo(TipoNotificacion.PAGO_COMPLETO)
                .monto(totalPagado)
                .mensaje(mensaje)
                .confirmada(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        notificacionRepo.save(notif);
    }

    /**
     * El admin registra un adelanto de sueldo.
     * Se crea una notificación de tipo ADELANTO con el monto específico.
     * El estibador debe confirmarla.
     */
    @Transactional
    public void registrarAdelanto(Long usuarioId, Double monto) {
        if (monto == null || monto <= 0) {
            throw new RuntimeException("El monto del adelanto debe ser mayor a cero");
        }

        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notificacion notif = Notificacion.builder()
                .usuario(usuario)
                .tipo(TipoNotificacion.ADELANTO)
                .monto(monto)
                .mensaje(String.format(
                        "El administrador te ha adelantado S/. %.2f de tu sueldo.",
                        monto
                ))
                .confirmada(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        notificacionRepo.save(notif);
    }

    /**
     * El estibador confirma una notificación pendiente.
     * Si es ADELANTO: se registra un gasto extra negativo para descontar del total.
     * Si es PAGO_COMPLETO: solo se confirma (las asistencias ya fueron marcadas).
     */
    @Transactional
    public void confirmarNotificacion(Long notificacionId, String usuarioNombre) {
        Notificacion notif = notificacionRepo.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        if (!notif.getUsuario().getUsuario().equals(usuarioNombre)) {
            throw new RuntimeException("No tienes permiso para confirmar esta notificación");
        }
        if (notif.getConfirmada()) {
            throw new RuntimeException("Esta notificación ya fue confirmada");
        }

        notif.setConfirmada(true);
        notif.setFechaConfirmacion(LocalDateTime.now());
        notificacionRepo.save(notif);
    }

    /**
     * Obtiene las notificaciones pendientes de confirmación de un usuario.
     */
    public List<Notificacion> obtenerPendientes(String usuarioNombre) {
        Usuario usuario = usuarioRepo.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificacionRepo.findByUsuarioAndConfirmadaFalse(usuario);
    }
}