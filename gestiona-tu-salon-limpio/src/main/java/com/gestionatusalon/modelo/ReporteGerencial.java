package com.gestionatusalon.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteGerencial {

    private int idReporte;
    private LocalDate fechaGeneracion;
    private String tipoReporte;

    public ReporteGerencial() {
    }

    public ReporteGerencial(int idReporte, LocalDate fechaGeneracion, String tipoReporte) {
        this.idReporte = idReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.tipoReporte = tipoReporte;
    }

    public double obtenerKPIOcupacionSede(int totalEspacios, int espaciosOcupados) {
        if (totalEspacios == 0) {
            return 0;
        }
        return (double) espaciosOcupados / totalEspacios * 100;
    }

    public List<String> listarEspaciosMasSolicitados(List<Reserva> reservas) {
        List<String> resultado = new ArrayList<>();

        if (reservas != null) {
            for (Reserva reserva : reservas) {
                resultado.add(reserva.getIdEspacio());
            }
        }

        return resultado;
    }

    public String exportarPDF() {
        return "Reporte exportado correctamente en formato PDF (simulado).";
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    @Override
    public String toString() {
        return "ReporteGerencial{" +
                "idReporte=" + idReporte +
                ", fechaGeneracion=" + fechaGeneracion +
                ", tipoReporte='" + tipoReporte + '\'' +
                '}';
    }
}