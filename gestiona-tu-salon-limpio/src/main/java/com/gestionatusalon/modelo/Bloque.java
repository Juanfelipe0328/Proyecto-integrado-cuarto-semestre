package com.gestionatusalon.modelo;

import java.util.ArrayList;
import java.util.List;

public class Bloque {

    private int idBloque;
    private String nombre;
    private int piso;
    private List<EspacioFisico> espacios;

    public Bloque() {
        this.espacios = new ArrayList<>();
    }

    public Bloque(int idBloque, String nombre, int piso) {
        this.idBloque = idBloque;
        this.nombre = nombre;
        this.piso = piso;
        this.espacios = new ArrayList<>();
    }

    public List<EspacioFisico> listarEspaciosPorPiso(int pisoConsultado) {
        if (this.piso == pisoConsultado) {
            return espacios;
        }
        return new ArrayList<>();
    }

    public void agregarEspacio(EspacioFisico espacio) {
        this.espacios.add(espacio);
    }

    public int getIdBloque() {
        return idBloque;
    }

    public void setIdBloque(int idBloque) {
        this.idBloque = idBloque;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public List<EspacioFisico> getEspacios() {
        return espacios;
    }

    public void setEspacios(List<EspacioFisico> espacios) {
        this.espacios = espacios;
    }

    @Override
    public String toString() {
        return "Bloque{" +
                "idBloque=" + idBloque +
                ", nombre='" + nombre + '\'' +
                ", piso=" + piso +
                '}';
    }
}