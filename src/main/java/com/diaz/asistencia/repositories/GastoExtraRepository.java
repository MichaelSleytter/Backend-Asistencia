package com.diaz.asistencia.repositories;

import com.diaz.asistencia.entities.GastoExtra;
import com.diaz.asistencia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoExtraRepository extends JpaRepository<GastoExtra, Long> {

    List<GastoExtra> findByUsuario(Usuario usuario);

}