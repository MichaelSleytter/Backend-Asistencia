package com.diaz.asistencia.repositories;

import com.diaz.asistencia.entities.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    // Solo almacenes activos para listar en el panel
    List<Almacen> findByEstadoTrue();
    List<Almacen> findByEstadoFalse();
}