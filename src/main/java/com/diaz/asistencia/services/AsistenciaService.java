package com.diaz.asistencia.services;

import com.diaz.asistencia.entities.*;
import com.diaz.asistencia.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlmacenRepository almacenRepository;
    private final GastoExtraRepository gastoExtraRepository;

    public AsistenciaService(
            AsistenciaRepository asistenciaRepository,
            UsuarioRepository usuarioRepository,
            AlmacenRepository almacenRepository,
            GastoExtraRepository gastoExtraRepository
    ) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.almacenRepository = almacenRepository;
        this.gastoExtraRepository = gastoExtraRepository;
    }

    @Transactional
    public Asistencia registrarAsistencia(String usuarioNombre, Long almacenId) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Almacen almacen = almacenRepository.findById(almacenId)
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));

        LocalDate hoy = LocalDate.now();

        asistenciaRepository.findByUsuarioAndFecha(usuario, hoy)
                .ifPresent(a -> { throw new RuntimeException("Ya registraste asistencia hoy"); });

        Asistencia asistencia = Asistencia.builder()
                .usuario(usuario)
                .almacen(almacen)
                .fecha(hoy)
                .pagoDia(almacen.getPagoBase())
                .estado(EstadoAsistencia.REGISTRADO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> obtenerPorUsuario(String usuarioNombre) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return asistenciaRepository.findByUsuario(usuario);
    }

    public List<GastoExtra> obtenerPagosExtraPorUsuario(String usuarioNombre) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return gastoExtraRepository.findByUsuario(usuario);
    }

    public List<Asistencia> obtenerTodas() {
        return asistenciaRepository.findAll();
    }

    /**
     * ✅ FIX: suma asistencias NO pagadas + pagos extra del usuario.
     * Antes solo sumaba pagoDia de asistencias y los pagos extra no aparecían.
     */
    public Double calcularTotalPorUsuario(String usuarioNombre) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        double totalAsistencias = asistenciaRepository.findByUsuario(usuario)
                .stream()
                .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                .mapToDouble(Asistencia::getPagoDia)
                .sum();

        double totalPagosExtra = gastoExtraRepository.findByUsuario(usuario)
                .stream()
                .mapToDouble(GastoExtra::getMonto)
                .sum();

        return totalAsistencias + totalPagosExtra;
    }

    public Long calcularDiasPendientes(String usuarioNombre) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return asistenciaRepository.findByUsuario(usuario)
                .stream()
                .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                .count();
    }

    public Double calcularTotalPorFechas(String usuarioNombre, LocalDate inicio, LocalDate fin) {
        Usuario usuario = usuarioRepository.findByUsuario(usuarioNombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return asistenciaRepository.findByUsuarioAndFechaBetween(usuario, inicio, fin)
                .stream()
                .filter(a -> a.getEstado() != EstadoAsistencia.PAGADO)
                .mapToDouble(Asistencia::getPagoDia)
                .sum();
    }
}