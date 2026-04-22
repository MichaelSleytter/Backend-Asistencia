package com.diaz.asistencia.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;

    private String password;

    // Guardado como String para mantener compatibilidad con el frontend y el JWT
    private String rol;

    // Borrado lógico: false = desactivado, sigue en BD para preservar historial
    @Builder.Default
    private Boolean activo = true;
}