package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumEstadoEspacio;

public class Auditorio extends EspacioFisico {

    private boolean tieneSonidoPro;
    private boolean tieneVideowall;

    public Auditorio() {
        super();
    }

    public Auditorio(String idEspacio, String nomenclatura, int capacidad,
                     EnumEstadoEspacio estado, boolean tieneSonidoPro, boolean tieneVideowall) {
        super(idEspacio, nomenclatura, capacidad, estado);
        this.tieneSonidoPro = tieneSonidoPro;
        this.tieneVideowall = tieneVideowall;
    }

    @Override
    public boolean validarIdoneidad(String requerimiento) {
        if (requerimiento == null || requerimiento.isEmpty()) {
            return true;
        }

        String req = requerimiento.toLowerCase();

        if (req.contains("sonido") && !tieneSonidoPro) {
            return false;
        }

        if (req.contains("videowall") && !tieneVideowall) {
            return false;
        }

        return true;
    }

    public boolean isTieneSonidoPro() {
        return tieneSonidoPro;
    }

    public void setTieneSonidoPro(boolean tieneSonidoPro) {
        this.tieneSonidoPro = tieneSonidoPro;
    }

    public boolean isTieneVideowall() {
        return tieneVideowall;
    }

    public void setTieneVideowall(boolean tieneVideowall) {
        this.tieneVideowall = tieneVideowall;
    }

    @Override
    public String toString() {
        return "Auditorio{" +
                "idEspacio='" + idEspacio + '\'' +
                ", nomenclatura='" + nomenclatura + '\'' +
                ", capacidad=" + capacidad +
                ", estado=" + estado +
                ", tieneSonidoPro=" + tieneSonidoPro +
                ", tieneVideowall=" + tieneVideowall +
                '}';
    }
}