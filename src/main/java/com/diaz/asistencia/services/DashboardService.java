package com.diaz.asistencia.services;

import com.diaz.asistencia.dtos.DashboardResponse;
import com.diaz.asistencia.entities.GastoExtra;
import com.diaz.asistencia.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final AsistenciaRepository asistenciaRepository;
    private final GastoExtraRepository gastoExtraRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlmacenRepository almacenRepository;

    public DashboardService(AsistenciaRepository asistenciaRepository,
                            GastoExtraRepository gastoExtraRepository,
                            UsuarioRepository usuarioRepository,
                            AlmacenRepository almacenRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.gastoExtraRepository = gastoExtraRepository;
        this.usuarioRepository = usuarioRepository;
        this.almacenRepository = almacenRepository;
    }

    public DashboardResponse obtenerDashboard() {

        // Total de días trabajados (suma de todos los pagos de asistencia)
        Double totalDias = asistenciaRepository.findAll()
                .stream()
                .mapToDouble(a -> a.getPagoDia())
                .sum();

        // Total de pagos extras
        List<GastoExtra> gastosExtra = gastoExtraRepository.findAll();
        Double totalGastosExtra = gastosExtra.stream()
                .mapToDouble(GastoExtra::getMonto)
                .sum();

        // Total = días trabajados + pagos extras
        Double totalPagado = totalDias + totalGastosExtra;

        Long totalAsistencias = asistenciaRepository.count();
        Long totalUsuarios = usuarioRepository.count();
        Long totalAlmacenes = almacenRepository.count();

        return DashboardResponse.builder()
                .totalPagado(totalPagado)
                .totalAsistencias(totalAsistencias)
                .totalUsuarios(totalUsuarios)
                .totalAlmacenes(totalAlmacenes)
                .build();
    }
}