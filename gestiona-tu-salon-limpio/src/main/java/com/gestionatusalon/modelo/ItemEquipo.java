package com.gestionatusalon.modelo;

public class ItemEquipo {

    private String serial;
    private String nombre;
    private String idEspacio;
    private boolean estadoFuncional;

    public ItemEquipo() {
    }

    public ItemEquipo(String serial, String nombre, String idEspacio, boolean estadoFuncional) {
        this.serial = serial;
        this.nombre = nombre;
        this.idEspacio = idEspacio;
        this.estadoFuncional = estadoFuncional;
    }

    public void registrarMantenimiento() {
        this.estadoFuncional = true;
        System.out.println("Mantenimiento registrado para el equipo: " + nombre);
    }

    public void trasladar(String nuevoEspacio) {
        this.idEspacio = nuevoEspacio;
        System.out.println("Equipo trasladado al espacio: " + nuevoEspacio);
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(String idEspacio) {
        this.idEspacio = idEspacio;
    }

    public boolean isEstadoFuncional() {
        return estadoFuncional;
    }

    public void setEstadoFuncional(boolean estadoFuncional) {
        this.estadoFuncional = estadoFuncional;
    }

    @Override
    public String toString() {
        return "ItemEquipo{" +
                "serial='" + serial + '\'' +
                ", nombre='" + nombre + '\'' +
                ", idEspacio='" + idEspacio + '\'' +
                ", estadoFuncional=" + estadoFuncional +
                '}';
    }
}