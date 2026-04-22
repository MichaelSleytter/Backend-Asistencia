package com.diaz.asistencia.controllers;

import com.diaz.asistencia.dtos.ReporteUsuarioDTO;
import com.diaz.asistencia.services.ReporteService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService){
        this.reporteService = reporteService;
    }

    @GetMapping("/usuarios")
    public List<ReporteUsuarioDTO> reporteUsuarios(){
        return reporteService.generarReporte();
    }
}