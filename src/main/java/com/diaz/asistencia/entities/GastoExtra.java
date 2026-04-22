package com.diaz.asistencia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="gastos_extra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String motivo;

    private Double monto;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name="usuario_id")
    private Usuario usuario;
}