package com.gestionatusalon.modelo;

import java.time.LocalTime;

public class FranjaHoraria {

    private int idFranja;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public FranjaHoraria() {
    }

    public FranjaHoraria(int idFranja, String diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        this.idFranja = idFranja;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public boolean esSolapada(FranjaHoraria otraFranja) {
        if (otraFranja == null) {
            return false;
        }

        if (!this.diaSemana.equalsIgnoreCase(otraFranja.getDiaSemana())) {
            return false;
        }

        return this.horaInicio.isBefore(otraFranja.getHoraFin()) &&
               this.horaFin.isAfter(otraFranja.getHoraInicio());
    }

    public int getIdFranja() {
        return idFranja;
    }

    public void setIdFranja(int idFranja) {
        this.idFranja = idFranja;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
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

    @Override
    public String toString() {
        return "FranjaHoraria{" +
                "idFranja=" + idFranja +
                ", diaSemana='" + diaSemana + '\'' +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}