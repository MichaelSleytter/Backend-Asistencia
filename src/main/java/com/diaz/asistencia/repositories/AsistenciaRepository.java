package com.diaz.asistencia.repositories;

import com.diaz.asistencia.entities.Asistencia;
import com.diaz.asistencia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByUsuario(Usuario usuario);

    Optional<Asistencia> findByUsuarioAndFecha(Usuario usuario, LocalDate fecha);

    List<Asistencia> findByUsuarioAndFechaBetween(Usuario usuario, LocalDate inicio, LocalDate fin);
}