package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumRol;

public class AuxiliarLogistico extends Usuario {

    private int zonaAsignada;
    private String especialidad;

    public AuxiliarLogistico() {
        this.rol = EnumRol.AUXILIAR_LOGISTICO;
    }

    public AuxiliarLogistico(int idUsuario, String nombre, String email, String passwordHash,
                             int zonaAsignada, String especialidad) {
        super(idUsuario, nombre, email, passwordHash, EnumRol.AUXILIAR_LOGISTICO);
        this.zonaAsignada = zonaAsignada;
        this.especialidad = especialidad;
    }

    public void gestionarOrden(int idOrden, String estado) {
        System.out.println("El auxiliar " + nombre + " gestionó la orden " + idOrden +
                " con estado: " + estado);
    }

    public void reportarIncidente(EspacioFisico espacio) {
        System.out.println("Incidente reportado en el espacio: " + espacio.getNomenclatura());
    }

    public int getZonaAsignada() {
        return zonaAsignada;
    }

    public void setZonaAsignada(int zonaAsignada) {
        this.zonaAsignada = zonaAsignada;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "AuxiliarLogistico{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", zonaAsignada=" + zonaAsignada +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}
