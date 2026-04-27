package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumRol;
import java.util.ArrayList;
import java.util.List;

public class Docente extends Usuario {

    private String facultad;
    private String escalafon;

    public Docente() {
        this.rol = EnumRol.DOCENTE;
    }

    public Docente(int idUsuario, String nombre, String email, String passwordHash,
                   String facultad, String escalafon) {
        super(idUsuario, nombre, email, passwordHash, EnumRol.DOCENTE);
        this.facultad = facultad;
        this.escalafon = escalafon;
    }

    public Reserva solicitarReserva(Reserva reserva) {
        return reserva;
    }

    public List<Reserva> consultarMisReservas() {
        return new ArrayList<>();
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public String getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }

    @Override
    public String toString() {
        return "Docente{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", facultad='" + facultad + '\'' +
                ", escalafon='" + escalafon + '\'' +
                '}';
    }
}