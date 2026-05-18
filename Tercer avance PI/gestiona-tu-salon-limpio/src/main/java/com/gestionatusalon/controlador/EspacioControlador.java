package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.EspacioFisicoDAO;
import com.gestionatusalon.modelo.EspacioFisico;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EspacioControlador {

    private final EspacioFisicoDAO espacioDAO;

    public EspacioControlador() {
        this.espacioDAO = new EspacioFisicoDAO();
    }

    public List<EspacioFisico> listarEspacios() {
        return espacioDAO.listarEspacios();
    }

    public List<EspacioFisico> listarEspaciosDisponibles(LocalDate fecha) {
        return espacioDAO.listarEspaciosDisponibles(Date.valueOf(fecha));
    }

    public boolean crearEspacio(String nombre, String bloque, String piso,
                                int capacidad, int idSede, int idTipoEspacio) {
        return espacioDAO.crearEspacio(nombre, bloque, piso, capacidad, idSede, idTipoEspacio);
    }

    public boolean cambiarEstadoEspacio(int idEspacio, String nuevoEstado) {
        return espacioDAO.cambiarEstadoEspacio(idEspacio, nuevoEstado);
    }

    public EspacioFisico buscarEspacioPorId(int idEspacio) {
        return espacioDAO.buscarPorId(idEspacio);
    }
}