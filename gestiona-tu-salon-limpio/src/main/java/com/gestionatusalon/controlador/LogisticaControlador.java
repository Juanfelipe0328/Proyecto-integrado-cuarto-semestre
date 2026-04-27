package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.OrdenServicioDAO;
import com.gestionatusalon.accesodatos.RequerimientoLogisticoDAO;
import com.gestionatusalon.modelo.OrdenServicio;
import com.gestionatusalon.modelo.RequerimientoLogistico;

import java.util.List;

public class LogisticaControlador {

    private final RequerimientoLogisticoDAO requerimientoDAO;
    private final OrdenServicioDAO ordenServicioDAO;

    public LogisticaControlador() {
        this.requerimientoDAO = new RequerimientoLogisticoDAO();
        this.ordenServicioDAO = new OrdenServicioDAO();
    }

    public boolean registrarRequerimiento(int idReserva, int sillasAdicionales,
                                          float configuracionAC, boolean requiereApoyoTecnico,
                                          Integer idCategoria) {

        RequerimientoLogistico req = new RequerimientoLogistico();
        req.setIdReserva(idReserva);
        req.setSillasAdicionales(sillasAdicionales);
        req.setConfiguracionAC(configuracionAC);
        req.setRequiereApoyoTecnico(requiereApoyoTecnico);

        return requerimientoDAO.registrarRequerimiento(req, idCategoria);
    }

    public List<RequerimientoLogistico> listarRequerimientosPorReserva(int idReserva) {
        return requerimientoDAO.listarPorReserva(idReserva);
    }

    public boolean crearOrdenServicio(int idReserva, int idAuxiliar,
                                      String estadoOrden, String observacion) {

        OrdenServicio orden = new OrdenServicio();
        orden.setIdAuxiliar(idAuxiliar);
        orden.setEstadoEntrega(estadoOrden);

        return ordenServicioDAO.crearOrden(idReserva, orden, observacion);
    }

    public List<OrdenServicio> listarOrdenesServicio() {
        return ordenServicioDAO.listarOrdenes();
    }

    public boolean actualizarEstadoOrden(int idOrden, String nuevoEstado) {
        return ordenServicioDAO.actualizarEstadoOrden(idOrden, nuevoEstado);
    }
}