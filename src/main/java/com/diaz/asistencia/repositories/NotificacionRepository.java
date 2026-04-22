package com.diaz.asistencia.repositories;

import com.diaz.asistencia.entities.Notificacion;
import com.diaz.asistencia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Notificaciones pendientes de un usuario (para mostrar el aviso)
    List<Notificacion> findByUsuarioAndConfirmadaFalse(Usuario usuario);

    // Todas las notificaciones de un usuario (historial)
    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
}