package com.diaz.asistencia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ReporteDiaDTO {

    private LocalDate fecha;
    private Double pagoDia;
    private Double extra;
    private String motivo;
    private Double totalDia;

}