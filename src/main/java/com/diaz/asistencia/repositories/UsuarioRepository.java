package com.diaz.asistencia.repositories;

import com.diaz.asistencia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    // Solo usuarios activos para listar en el panel
    List<Usuario> findByActivoTrue();
    List<Usuario> findByActivoFalse();
}