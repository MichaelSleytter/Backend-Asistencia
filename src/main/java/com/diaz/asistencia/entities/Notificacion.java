package com.diaz.asistencia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // ADELANTO o PAGO_COMPLETO
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    private Double monto;

    private String mensaje;

    // false = pendiente de confirmar, true = confirmada por el estibador
    @Builder.Default
    private Boolean confirmada = false;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaConfirmacion;
}