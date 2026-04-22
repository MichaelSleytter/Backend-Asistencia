package com.diaz.asistencia.controllers;

import com.diaz.asistencia.dtos.DashboardResponse;
import com.diaz.asistencia.services.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse obtenerDashboard() {
        return dashboardService.obtenerDashboard();
    }
}