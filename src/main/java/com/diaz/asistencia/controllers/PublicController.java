package com.diaz.asistencia.controllers;

import com.diaz.asistencia.entities.Almacen;
import com.diaz.asistencia.repositories.AlmacenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final AlmacenRepository almacenRepository;

    public PublicController(AlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    // Solo retorna almacenes activos (estado = true)
    @GetMapping("/almacenes")
    public List<Almacen> listarAlmacenes() {
        return almacenRepository.findByEstadoTrue();
    }
}