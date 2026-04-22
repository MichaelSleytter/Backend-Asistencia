package com.diaz.asistencia.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "almacenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String ubicacion;

    private Double pagoBase;

    @Builder.Default
    private Boolean estado = true;
}