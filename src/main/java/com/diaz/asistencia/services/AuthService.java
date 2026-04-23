package com.diaz.asistencia.services;

import com.diaz.asistencia.entities.Usuario;
import com.diaz.asistencia.repositories.UsuarioRepository;
import com.diaz.asistencia.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       JwtService jwtService,
                       BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService        = jwtService;
        this.passwordEncoder   = passwordEncoder;
    }

    public Map<String, String> login(String usuario, String password) {

        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!user.getActivo()) {
            throw new RuntimeException("Usuario desactivado");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 🔥 Generar token con rol
        String token = jwtService.generarToken(
                user.getUsuario(),
                user.getRol()
        );

        // 🔥 Retornar token + rol (CLAVE PARA FRONTEND)
        return Map.of(
                "token", token,
                "rol", user.getRol()
        );
    }
}