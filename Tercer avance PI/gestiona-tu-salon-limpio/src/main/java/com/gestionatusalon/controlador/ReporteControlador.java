package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.EspacioFisicoDAO;
import com.gestionatusalon.accesodatos.ReservaDAO;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.ReporteGerencial;
import com.gestionatusalon.modelo.Reserva;

import java.time.LocalDate;
import java.util.List;

public class ReporteControlador {

    private final ReservaDAO reservaDAO;
    private final EspacioFisicoDAO espacioDAO;

    public ReporteControlador() {
        this.reservaDAO = new ReservaDAO();
        this.espacioDAO = new EspacioFisicoDAO();
    }

    public ReporteGerencial generarReporteBasico(String tipoReporte) {
        ReporteGerencial reporte = new ReporteGerencial();
        reporte.setIdReporte(1);
        reporte.setFechaGeneracion(LocalDate.now());
        reporte.setTipoReporte(tipoReporte);
        return reporte;
    }

    public double calcularKPIOcupacion() {
        List<EspacioFisico> espacios = espacioDAO.listarEspacios();
        List<Reserva> reservas = reservaDAO.listarReservas();

        int totalEspacios = espacios.size();
        int espaciosOcupados = reservas.size();

        ReporteGerencial reporte = new ReporteGerencial();
        return reporte.obtenerKPIOcupacionSede(totalEspacios, espaciosOcupados);
    }

    public List<String> obtenerEspaciosMasSolicitados() {
        List<Reserva> reservas = reservaDAO.listarReservas();
        ReporteGerencial reporte = new ReporteGerencial();
        return reporte.listarEspaciosMasSolicitados(reservas);
    }

    public String exportarReportePDF() {
        ReporteGerencial reporte = new ReporteGerencial();
        return reporte.exportarPDF();
    }
}