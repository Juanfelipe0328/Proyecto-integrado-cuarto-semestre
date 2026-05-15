package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumEstadoEspacio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sede {

    private int idSede;
    private String nombre;
    private String ciudad;
    private String direccion;
    private List<Bloque> bloques;

    public Sede() {
        this.bloques = new ArrayList<>();
    }

    public Sede(int idSede, String nombre, String ciudad, String direccion) {
        this.idSede = idSede;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.bloques = new ArrayList<>();
    }

    public List<EspacioFisico> obtenerEspaciosDisponibles(LocalDate fecha) {
        List<EspacioFisico> disponibles = new ArrayList<>();

        for (Bloque bloque : bloques) {
            for (EspacioFisico espacio : bloque.getEspacios()) {
                if (espacio.getEstado() == EnumEstadoEspacio.DISPONIBLE) {
                    disponibles.add(espacio);
                }
            }
        }

        return disponibles;
    }

    public int calcularCapacidadTotal() {
        int total = 0;

        for (Bloque bloque : bloques) {
            for (EspacioFisico espacio : bloque.getEspacios()) {
                total += espacio.getCapacidad();
            }
        }

        return total;
    }

    public void generarReporteSede() {
        System.out.println("Reporte de la sede: " + nombre);
        System.out.println("Ciudad: " + ciudad);
        System.out.println("Dirección: " + direccion);
        System.out.println("Cantidad de bloques: " + bloques.size());
        System.out.println("Capacidad total: " + calcularCapacidadTotal());
    }

    public void agregarBloque(Bloque bloque) {
        this.bloques.add(bloque);
    }

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Bloque> getBloques() {
        return bloques;
    }

    public void setBloques(List<Bloque> bloques) {
        this.bloques = bloques;
    }

    @Override
    public String toString() {
        return "Sede{" +
                "idSede=" + idSede +
                ", nombre='" + nombre + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}