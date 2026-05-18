package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumEstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private int idReserva;
    private int idDocente;
    private String idEspacio;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EnumEstadoReserva estado;

    public Reserva() {
    }

    public Reserva(int idReserva, int idDocente, String idEspacio, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin, EnumEstadoReserva estado) {
        this.idReserva = idReserva;
        this.idDocente = idDocente;
        this.idEspacio = idEspacio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public boolean validarConflictoHorario(Reserva otraReserva) {
        if (otraReserva == null) {
            return false;
        }

        if (!this.idEspacio.equalsIgnoreCase(otraReserva.getIdEspacio())) {
            return false;
        }

        if (!this.fecha.equals(otraReserva.getFecha())) {
            return false;
        }

        if (this.horaInicio == null || this.horaFin == null ||
            otraReserva.getHoraInicio() == null || otraReserva.getHoraFin() == null) {
            return false;
        }

        return this.horaInicio.isBefore(otraReserva.getHoraFin()) &&
               this.horaFin.isAfter(otraReserva.getHoraInicio());
    }

    public void confirmarReserva() {
        this.estado = EnumEstadoReserva.CONFIRMADA;
    }

    public void cancelarReserva(String motivo) {
        this.estado = EnumEstadoReserva.CANCELADA;
        System.out.println("Reserva cancelada. Motivo: " + motivo);
    }

    public void notificarPartes() {
        System.out.println("Notificación enviada al docente y al personal logístico.");
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(String idEspacio) {
        this.idEspacio = idEspacio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public EnumEstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EnumEstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", idDocente=" + idDocente +
                ", idEspacio='" + idEspacio + '\'' +
                ", fecha=" + fecha +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", estado=" + estado +
                '}';
    }
}