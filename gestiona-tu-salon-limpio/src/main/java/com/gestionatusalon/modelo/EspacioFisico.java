package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumEstadoEspacio;
import java.util.ArrayList;
import java.util.List;

public abstract class EspacioFisico {

    protected String idEspacio;
    protected String nomenclatura;
    protected int capacidad;
    protected EnumEstadoEspacio estado;
    protected List<ItemEquipo> equipos;

    public EspacioFisico() {
        this.equipos = new ArrayList<>();
    }

    public EspacioFisico(String idEspacio, String nomenclatura, int capacidad, EnumEstadoEspacio estado) {
        this.idEspacio = idEspacio;
        this.nomenclatura = nomenclatura;
        this.capacidad = capacidad;
        this.estado = estado;
        this.equipos = new ArrayList<>();
    }

    public abstract boolean validarIdoneidad(String requerimiento);

    public List<ItemEquipo> obtenerEquipamiento() {
        return equipos;
    }

    public void agregarEquipo(ItemEquipo equipo) {
        this.equipos.add(equipo);
    }

    public void cambiarEstado(EnumEstadoEspacio nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(String idEspacio) {
        this.idEspacio = idEspacio;
    }

    public String getNomenclatura() {
        return nomenclatura;
    }

    public void setNomenclatura(String nomenclatura) {
        this.nomenclatura = nomenclatura;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public EnumEstadoEspacio getEstado() {
        return estado;
    }

    public void setEstado(EnumEstadoEspacio estado) {
        this.estado = estado;
    }

    public List<ItemEquipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<ItemEquipo> equipos) {
        this.equipos = equipos;
    }

    @Override
    public String toString() {
        return "EspacioFisico{" +
                "idEspacio='" + idEspacio + '\'' +
                ", nomenclatura='" + nomenclatura + '\'' +
                ", capacidad=" + capacidad +
                ", estado=" + estado +
                '}';
    }
}
