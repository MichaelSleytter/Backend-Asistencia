package com.diaz.asistencia.dtos;

import com.diaz.asistencia.entities.GastoExtra;
import java.util.List;

public class ReporteUsuarioDTO {

    private String usuario;
    private Long diasTrabajados;
    private Double totalGanado;
    private Double pagosExtra;
    private Double adelantos;
    private Double neto;
    // ✅ detalle de cada pago extra con motivo y monto para el admin
    private List<PagoExtraDetalle> detallePagosExtra;

    public ReporteUsuarioDTO(
            String usuario,
            Long diasTrabajados,
            Double totalGanado,
            Double pagosExtra,
            Double adelantos,
            List<GastoExtra> listaGastos
    ) {
        this.usuario = usuario;
        this.diasTrabajados = diasTrabajados;
        this.totalGanado = totalGanado;
        this.pagosExtra = pagosExtra;
        this.adelantos = adelantos;
        this.neto = totalGanado + pagosExtra - adelantos;
        this.detallePagosExtra = listaGastos.stream()
                .map(g -> new PagoExtraDetalle(g.getMotivo(), g.getMonto(), g.getFecha() != null ? g.getFecha().toString() : ""))
                .toList();
    }

    public String getUsuario() { return usuario; }
    public Long getDiasTrabajados() { return diasTrabajados; }
    public Double getTotalGanado() { return totalGanado; }
    public Double getPagosExtra() { return pagosExtra; }
    public Double getAdelantos() { return adelantos; }
    public Double getNeto() { return neto; }
    public List<PagoExtraDetalle> getDetallePagosExtra() { return detallePagosExtra; }

    // Clase interna para serializar el detalle
    public static class PagoExtraDetalle {
        private String motivo;
        private Double monto;
        private String fecha;

        public PagoExtraDetalle(String motivo, Double monto, String fecha) {
            this.motivo = motivo;
            this.monto = monto;
            this.fecha = fecha;
        }

        public String getMotivo() { return motivo; }
        public Double getMonto() { return monto; }
        public String getFecha() { return fecha; }
    }
}