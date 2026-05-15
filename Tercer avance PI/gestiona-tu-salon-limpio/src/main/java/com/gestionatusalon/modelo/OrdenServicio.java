package com.gestionatusalon.modelo;

import java.time.LocalTime;

public class OrdenServicio {

    private int idOrden;
    private int idAuxiliar;
    private String estadoEntrega;
    private LocalTime horaLimite;

    public OrdenServicio() {
    }

    public OrdenServicio(int idOrden, int idAuxiliar, String estadoEntrega, LocalTime horaLimite) {
        this.idOrden = idOrden;
        this.idAuxiliar = idAuxiliar;
        this.estadoEntrega = estadoEntrega;
        this.horaLimite = horaLimite;
    }

    public void marcarComoCompletado() {
        this.estadoEntrega = "COMPLETADA";
    }

    public void solicitarAyudaExtra(String motivo) {
        System.out.println("Se solicitó ayuda extra. Motivo: " + motivo);
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdAuxiliar() {
        return idAuxiliar;
    }

    public void setIdAuxiliar(int idAuxiliar) {
        this.idAuxiliar = idAuxiliar;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public LocalTime getHoraLimite() {
        return horaLimite;
    }

    public void setHoraLimite(LocalTime horaLimite) {
        this.horaLimite = horaLimite;
    }

    @Override
    public String toString() {
        return "OrdenServicio{" +
                "idOrden=" + idOrden +
                ", idAuxiliar=" + idAuxiliar +
                ", estadoEntrega='" + estadoEntrega + '\'' +
                ", horaLimite=" + horaLimite +
                '}';
    }
}