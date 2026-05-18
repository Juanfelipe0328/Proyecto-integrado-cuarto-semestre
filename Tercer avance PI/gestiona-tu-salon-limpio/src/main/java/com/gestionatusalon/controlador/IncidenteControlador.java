package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.IncidenteDAO;
import com.gestionatusalon.modelo.Incidente;

import java.time.LocalDateTime;
import java.util.List;

public class IncidenteControlador {

    private final IncidenteDAO incidenteDAO;

    public IncidenteControlador() {
        this.incidenteDAO = new IncidenteDAO();
    }

    public List<Incidente> listarIncidentes() {
        return incidenteDAO.listarIncidentes();
    }

    public boolean registrarIncidente(int idUsuarioReporta, String descripcion,
                                      int severidad, Integer idEspacio, Integer idEquipo) {

        Incidente incidente = new Incidente();
        incidente.setIdUsuarioReporta(idUsuarioReporta);
        incidente.setDescripcion(descripcion);
        incidente.setSeveridad(severidad);
        incidente.setFechaReporte(LocalDateTime.now());

        return incidenteDAO.registrarIncidente(incidente, idEspacio, idEquipo);
    }

    public boolean cerrarIncidente(int idIncidente) {
        return incidenteDAO.cerrarIncidente(idIncidente);
    }
}