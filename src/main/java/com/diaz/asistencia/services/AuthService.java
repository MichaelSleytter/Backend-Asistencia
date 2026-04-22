package com.diaz.asistencia.services;

import com.diaz.asistencia.entities.Usuario;
import com.diaz.asistencia.repositories.UsuarioRepository;
import com.diaz.asistencia.security.JwtService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public Map<String, String> login(String usuario, String password) {

        // 🔥 Buscar usuario en BD
        Usuario user = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        // 🔥 Validar password
        if (!user.getPassword().equals(password)) {
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