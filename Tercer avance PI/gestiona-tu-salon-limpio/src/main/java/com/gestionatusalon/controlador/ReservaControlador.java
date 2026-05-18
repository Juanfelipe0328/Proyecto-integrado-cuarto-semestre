package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.ReservaDAO;
import com.gestionatusalon.enumeraciones.EnumEstadoReserva;
import com.gestionatusalon.modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaControlador {

    private final ReservaDAO reservaDAO;

    public ReservaControlador() {
        this.reservaDAO = new ReservaDAO();
    }

    public List<Reserva> listarReservas() {
        return reservaDAO.listarReservas();
    }

    public List<Reserva> listarReservasPorUsuario(int idUsuario) {
        return reservaDAO.listarReservasPorUsuario(idUsuario);
    }

    public boolean crearReserva(int idDocente, String idEspacio, LocalDate fecha,
                                LocalTime horaInicio, LocalTime horaFin,
                                List<Integer> bloques, String observacion) {

        Reserva reserva = new Reserva();
        reserva.setIdDocente(idDocente);
        reserva.setIdEspacio(idEspacio);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(horaInicio);
        reserva.setHoraFin(horaFin);
        reserva.setEstado(EnumEstadoReserva.PENDIENTE);

        return reservaDAO.crearReservaConBloques(reserva, bloques, observacion);
    }

    public boolean cancelarReserva(int idReserva, String motivo) {
        return reservaDAO.cancelarReserva(idReserva, motivo);
    }

    public boolean aprobarReserva(int idReserva) {
        return reservaDAO.aprobarReserva(idReserva);
    }

    public boolean validarConflictoSimple(Reserva nuevaReserva, List<Reserva> reservasExistentes) {
        for (Reserva reservaExistente : reservasExistentes) {
            if (nuevaReserva.validarConflictoHorario(reservaExistente)) {
                return true;
            }
        }
        return false;
    }
}