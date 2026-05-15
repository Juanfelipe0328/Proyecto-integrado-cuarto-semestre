package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumEstadoEspacio;
import java.util.ArrayList;
import java.util.List;

public class Laboratorio extends EspacioFisico {

    private List<String> softwareInstalado;
    private int capacidadEquipos;

    public Laboratorio() {
        super();
        this.softwareInstalado = new ArrayList<>();
    }

    public Laboratorio(String idEspacio, String nomenclatura, int capacidad,
                       EnumEstadoEspacio estado, List<String> softwareInstalado, int capacidadEquipos) {
        super(idEspacio, nomenclatura, capacidad, estado);
        this.softwareInstalado = softwareInstalado;
        this.capacidadEquipos = capacidadEquipos;
    }

    @Override
    public boolean validarIdoneidad(String requerimiento) {
        if (softwareInstalado == null) {
            return false;
        }

        for (String software : softwareInstalado) {
            if (software.equalsIgnoreCase(requerimiento)) {
                return true;
            }
        }
        return false;
    }

    public boolean verificarRequisitosTecnicos(String req) {
        return validarIdoneidad(req);
    }

    public List<String> getSoftwareInstalado() {
        return softwareInstalado;
    }

    public void setSoftwareInstalado(List<String> softwareInstalado) {
        this.softwareInstalado = softwareInstalado;
    }

    public int getCapacidadEquipos() {
        return capacidadEquipos;
    }

    public void setCapacidadEquipos(int capacidadEquipos) {
        this.capacidadEquipos = capacidadEquipos;
    }

    @Override
    public String toString() {
        return "Laboratorio{" +
                "idEspacio='" + idEspacio + '\'' +
                ", nomenclatura='" + nomenclatura + '\'' +
                ", capacidad=" + capacidad +
                ", estado=" + estado +
                ", capacidadEquipos=" + capacidadEquipos +
                ", softwareInstalado=" + softwareInstalado +
                '}';
    }
}