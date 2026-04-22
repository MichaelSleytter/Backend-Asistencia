package com.diaz.asistencia.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "asistencias",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "fecha"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    private LocalDate fecha;

    private Double pagoDia;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;

    private LocalDateTime fechaRegistro;
}