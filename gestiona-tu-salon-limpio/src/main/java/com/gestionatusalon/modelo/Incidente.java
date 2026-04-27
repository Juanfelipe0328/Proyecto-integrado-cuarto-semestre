package com.gestionatusalon.modelo;

import java.time.LocalDateTime;

public class Incidente {

    private int idIncidente;
    private int idUsuarioReporta;
    private String descripcion;
    private int severidad;
    private LocalDateTime fechaReporte;

    public Incidente() {
    }

    public Incidente(int idIncidente, int idUsuarioReporta, String descripcion,
                     int severidad, LocalDateTime fechaReporte) {
        this.idIncidente = idIncidente;
        this.idUsuarioReporta = idUsuarioReporta;
        this.descripcion = descripcion;
        this.severidad = severidad;
        this.fechaReporte = fechaReporte;
    }

    public String asignarPrioridad() {
        if (severidad >= 8) {
            return "ALTA";
        } else if (severidad >= 5) {
            return "MEDIA";
        } else {
            return "BAJA";
        }
    }

    public void cerrarTicket(String resolucion) {
        System.out.println("Incidente cerrado. Resolución: " + resolucion);
    }

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public int getIdUsuarioReporta() {
        return idUsuarioReporta;
    }

    public void setIdUsuarioReporta(int idUsuarioReporta) {
        this.idUsuarioReporta = idUsuarioReporta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getSeveridad() {
        return severidad;
    }

    public void setSeveridad(int severidad) {
        this.severidad = severidad;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    @Override
    public String toString() {
        return "Incidente{" +
                "idIncidente=" + idIncidente +
                ", idUsuarioReporta=" + idUsuarioReporta +
                ", descripcion='" + descripcion + '\'' +
                ", severidad=" + severidad +
                ", fechaReporte=" + fechaReporte +
                '}';
    }
}