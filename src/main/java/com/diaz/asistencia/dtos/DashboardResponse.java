package com.diaz.asistencia.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {

    private Double totalPagado;
    private Long totalAsistencias;
    private Long totalUsuarios;
    private Long totalAlmacenes;
}