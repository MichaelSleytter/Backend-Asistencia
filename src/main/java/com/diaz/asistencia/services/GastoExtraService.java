package com.diaz.asistencia.services;

import com.diaz.asistencia.entities.GastoExtra;
import com.diaz.asistencia.entities.Usuario;
import com.diaz.asistencia.repositories.GastoExtraRepository;
import com.diaz.asistencia.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GastoExtraService {

    private final GastoExtraRepository gastoRepo;
    private final UsuarioRepository usuarioRepo;

    public GastoExtraService(
            GastoExtraRepository gastoRepo,
            UsuarioRepository usuarioRepo
    ) {
        this.gastoRepo = gastoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public void registrar(String username, String motivo, Double monto) {

        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("El motivo no puede estar vacío");
        }
        if (monto == null || monto <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }

        Usuario usuario = usuarioRepo
                .findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        GastoExtra pago = new GastoExtra();
        pago.setUsuario(usuario);
        pago.setMotivo(motivo);
        pago.setMonto(monto);
        pago.setFecha(LocalDate.now());

        gastoRepo.save(pago);
    }
}