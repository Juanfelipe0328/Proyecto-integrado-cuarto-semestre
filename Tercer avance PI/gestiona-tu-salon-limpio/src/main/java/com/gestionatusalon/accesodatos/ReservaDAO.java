package com.gestionatusalon.accesodatos;

import com.gestionatusalon.enumeraciones.EnumEstadoReserva;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public List<Reserva> listarReservas() {
        List<Reserva> reservas = new ArrayList<>();

        String sql = """
                SELECT r.id_reserva, r.id_usuario, r.id_espacio, r.fecha_reserva, r.estado_reserva,
                       MIN(bh.hora_inicio) AS hora_inicio, MAX(bh.hora_fin) AS hora_fin
                FROM Reserva r
                LEFT JOIN Reserva_Bloque rb ON r.id_reserva = rb.id_reserva
                LEFT JOIN Bloque_Horario bh ON rb.id_bloque = bh.id_bloque
                GROUP BY r.id_reserva, r.id_usuario, r.id_espacio, r.fecha_reserva, r.estado_reserva
                ORDER BY r.id_reserva
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reservas.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }

        return reservas;
    }

    public List<Reserva> listarReservasPorUsuario(int idUsuario) {
        List<Reserva> reservas = new ArrayList<>();

        String sql = """
                SELECT r.id_reserva, r.id_usuario, r.id_espacio, r.fecha_reserva, r.estado_reserva,
                       MIN(bh.hora_inicio) AS hora_inicio, MAX(bh.hora_fin) AS hora_fin
                FROM Reserva r
                LEFT JOIN Reserva_Bloque rb ON r.id_reserva = rb.id_reserva
                LEFT JOIN Bloque_Horario bh ON rb.id_bloque = bh.id_bloque
                WHERE r.id_usuario = ?
                GROUP BY r.id_reserva, r.id_usuario, r.id_espacio, r.fecha_reserva, r.estado_reserva
                ORDER BY r.id_reserva
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapearReserva(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar reservas por usuario: " + e.getMessage());
        }

        return reservas;
    }

    public boolean crearReservaConBloques(Reserva reserva, List<Integer> bloques, String observacion) {
        String sqlReserva = """
                INSERT INTO Reserva (fecha_reserva, estado_reserva, observacion, id_usuario, id_espacio)
                VALUES (?, ?, ?, ?, ?)
                """;

        String sqlBloque = """
                INSERT INTO Reserva_Bloque (id_reserva, id_bloque)
                VALUES (?, ?)
                """;

        Connection conexion = null;
        PreparedStatement psReserva = null;
        PreparedStatement psBloque = null;
        ResultSet rs = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            psReserva = conexion.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS);
            psReserva.setDate(1, Date.valueOf(reserva.getFecha()));
            psReserva.setString(2, convertirEstadoTexto(reserva.getEstado()));
            psReserva.setString(3, observacion);
            psReserva.setInt(4, reserva.getIdDocente());
            psReserva.setInt(5, Integer.parseInt(reserva.getIdEspacio()));

            int filas = psReserva.executeUpdate();

            if (filas == 0) {
                conexion.rollback();
                return false;
            }

            rs = psReserva.getGeneratedKeys();
            int idReservaGenerada = 0;

            if (rs.next()) {
                idReservaGenerada = rs.getInt(1);
            }

            if (bloques != null && !bloques.isEmpty()) {
                psBloque = conexion.prepareStatement(sqlBloque);

                for (Integer idBloque : bloques) {
                    psBloque.setInt(1, idReservaGenerada);
                    psBloque.setInt(2, idBloque);
                    psBloque.addBatch();
                }

                psBloque.executeBatch();
            }

            conexion.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al revertir transacción: " + ex.getMessage());
            }

            System.out.println("Error al crear reserva: " + e.getMessage());
            return false;

        } finally {
            try {
                if (rs != null) rs.close();
                if (psBloque != null) psBloque.close();
                if (psReserva != null) psReserva.close();
                if (conexion != null) {
                    conexion.setAutoCommit(true);
                    conexion.close();
                }
            } catch (SQLException e) {
                System.out.println("Error cerrando recursos: " + e.getMessage());
            }
        }
    }

    public boolean cancelarReserva(int idReserva, String motivo) {
        String sql = """
                UPDATE Reserva
                SET estado_reserva = 'Cancelada', motivo_cancelacion = ?
                WHERE id_reserva = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, motivo);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cancelar reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean aprobarReserva(int idReserva) {
        String sql = "UPDATE Reserva SET estado_reserva = 'Aprobada' WHERE id_reserva = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al aprobar reserva: " + e.getMessage());
            return false;
        }
    }

    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setIdDocente(rs.getInt("id_usuario"));
        reserva.setIdEspacio(String.valueOf(rs.getInt("id_espacio")));
        reserva.setFecha(rs.getDate("fecha_reserva").toLocalDate());

        Time inicio = rs.getTime("hora_inicio");
        Time fin = rs.getTime("hora_fin");

        reserva.setHoraInicio(inicio != null ? inicio.toLocalTime() : null);
        reserva.setHoraFin(fin != null ? fin.toLocalTime() : null);
        reserva.setEstado(convertirEstado(rs.getString("estado_reserva")));

        return reserva;
    }

    private EnumEstadoReserva convertirEstado(String estadoTexto) {
        if (estadoTexto == null) {
            return EnumEstadoReserva.PENDIENTE;
        }

        return switch (estadoTexto.toLowerCase()) {
            case "aprobada" -> EnumEstadoReserva.CONFIRMADA;
            case "cancelada" -> EnumEstadoReserva.CANCELADA;
            case "finalizada" -> EnumEstadoReserva.FINALIZADA;
            default -> EnumEstadoReserva.PENDIENTE;
        };
    }

    private String convertirEstadoTexto(EnumEstadoReserva estado) {
        if (estado == null) {
            return "Pendiente";
        }

        return switch (estado) {
            case CONFIRMADA -> "Aprobada";
            case CANCELADA -> "Cancelada";
            case FINALIZADA -> "Finalizada";
            default -> "Pendiente";
        };
    }
}
