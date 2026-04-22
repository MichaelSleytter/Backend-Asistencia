package com.diaz.asistencia.services;

import com.diaz.asistencia.dtos.ReporteUsuarioDTO;
import com.diaz.asistencia.entities.*;
import com.diaz.asistencia.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    private final UsuarioRepository usuarioRepo;
    private final AsistenciaRepository asistenciaRepo;
    private final GastoExtraRepository gastoRepo;
    private final NotificacionRepository notificacionRepo;

    public ReporteService(
            UsuarioRepository usuarioRepo,
            AsistenciaRepository asistenciaRepo,
            GastoExtraRepository gastoRepo,
            NotificacionRepository notificacionRepo
    ) {
        this.usuarioRepo = usuarioRepo;
        this.asistenciaRepo = asistenciaRepo;
        this.gastoRepo = gastoRepo;
        this.notificacionRepo = notificacionRepo;
    }

    public List<ReporteUsuarioDTO> generarReporte() {
        // ✅ FIX: solo usuarios activos — antes usaba findAll() y aparecían usuarios de BDs viejas
        List<Usuario> usuarios = usuarioRepo.findByActivoTrue();
        List<ReporteUsuarioDTO> reporte = new ArrayList<>();

        for (Usuario u : usuarios) {
            List<Asistencia> asistencias = asistenciaRepo.findByUsuario(u);
            List<GastoExtra> pagosExtra  = gastoRepo.findByUsuario(u);
            List<Notificacion> notifs    = notificacionRepo.findByUsuarioOrderByFechaCreacionDesc(u);

            long dias = asistencias.stream()
                    .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                    .count();

            double totalPendiente = asistencias.stream()
                    .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                    .mapToDouble(Asistencia::getPagoDia)
                    .sum();

            double totalPagosExtra = pagosExtra.stream()
                    .mapToDouble(GastoExtra::getMonto)
                    .sum();

            double totalAdelantos = notifs.stream()
                    .filter(n -> n.getTipo() == TipoNotificacion.ADELANTO && n.getConfirmada())
                    .mapToDouble(Notificacion::getMonto)
                    .sum();

            reporte.add(new ReporteUsuarioDTO(
                    u.getUsuario(),
                    dias,
                    totalPendiente,
                    totalPagosExtra,
                    totalAdelantos,
                    pagosExtra   // ✅ lista completa para mostrar motivo en el admin
            ));
        }

        return reporte;
    }
}