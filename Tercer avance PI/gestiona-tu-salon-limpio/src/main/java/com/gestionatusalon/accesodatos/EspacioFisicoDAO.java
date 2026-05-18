package com.gestionatusalon.accesodatos;

import com.gestionatusalon.enumeraciones.EnumEstadoEspacio;
import com.gestionatusalon.modelo.Auditorio;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Laboratorio;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspacioFisicoDAO {

    public List<EspacioFisico> listarEspacios() {
        List<EspacioFisico> espacios = new ArrayList<>();

        String sql = """
                SELECT e.id_espacio, e.nombre_espacio, e.capacidad, e.estado, t.nombre_tipo
                FROM Espacio e
                INNER JOIN Tipo_Espacio t ON e.id_tipo_espacio = t.id_tipo_espacio
                ORDER BY e.id_espacio
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                espacios.add(crearObjetoEspacio(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar espacios: " + e.getMessage());
        }

        return espacios;
    }

    public List<EspacioFisico> listarEspaciosDisponibles(Date fechaReserva) {
        List<EspacioFisico> espacios = new ArrayList<>();

        String sql = """
                SELECT e.id_espacio, e.nombre_espacio, e.capacidad, e.estado, t.nombre_tipo
                FROM Espacio e
                INNER JOIN Tipo_Espacio t ON e.id_tipo_espacio = t.id_tipo_espacio
                WHERE e.estado = 'Disponible'
                  AND e.id_espacio NOT IN (
                      SELECT r.id_espacio
                      FROM Reserva r
                      WHERE r.fecha_reserva = ?
                        AND r.estado_reserva IN ('Pendiente', 'Aprobada')
                  )
                ORDER BY e.id_espacio
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setDate(1, fechaReserva);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    espacios.add(crearObjetoEspacio(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar espacios disponibles: " + e.getMessage());
        }

        return espacios;
    }

    public boolean crearEspacio(String nombre, String bloque, String piso,
                                int capacidad, int idSede, int idTipoEspacio) {
        String sql = """
                INSERT INTO Espacio (nombre_espacio, bloque, piso, capacidad, estado, id_sede, id_tipo_espacio)
                VALUES (?, ?, ?, ?, 'Disponible', ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, bloque);
            ps.setString(3, piso);
            ps.setInt(4, capacidad);
            ps.setInt(5, idSede);
            ps.setInt(6, idTipoEspacio);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al crear espacio: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstadoEspacio(int idEspacio, String nuevoEstado) {
        String sql = "UPDATE Espacio SET estado = ? WHERE id_espacio = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEspacio);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado del espacio: " + e.getMessage());
            return false;
        }
    }

    public EspacioFisico buscarPorId(int idEspacio) {
        String sql = """
                SELECT e.id_espacio, e.nombre_espacio, e.capacidad, e.estado, t.nombre_tipo
                FROM Espacio e
                INNER JOIN Tipo_Espacio t ON e.id_tipo_espacio = t.id_tipo_espacio
                WHERE e.id_espacio = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearObjetoEspacio(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar espacio por ID: " + e.getMessage());
        }

        return null;
    }

    private EspacioFisico crearObjetoEspacio(ResultSet rs) throws SQLException {
        String tipo = rs.getString("nombre_tipo");
        String estadoTexto = rs.getString("estado");

        EnumEstadoEspacio estado = convertirEstado(estadoTexto);

        EspacioFisico espacio;

        if (tipo != null && tipo.toLowerCase().contains("laboratorio")) {
            espacio = new Laboratorio();
        } else {
            espacio = new Auditorio();
        }

        espacio.setIdEspacio(String.valueOf(rs.getInt("id_espacio")));
        espacio.setNomenclatura(rs.getString("nombre_espacio"));
        espacio.setCapacidad(rs.getInt("capacidad"));
        espacio.setEstado(estado);
        // Guardamos el tipo real de la BD en un campo auxiliar usando el nombre
        espacio.setTipoNombre(tipo != null ? tipo : "Sin tipo");

        return espacio;
    }

    private EnumEstadoEspacio convertirEstado(String estadoTexto) {
        if (estadoTexto == null) {
            return EnumEstadoEspacio.INACTIVO;
        }

        return switch (estadoTexto.toLowerCase()) {
            case "disponible" -> EnumEstadoEspacio.DISPONIBLE;
            case "ocupado" -> EnumEstadoEspacio.OCUPADO;
            case "mantenimiento" -> EnumEstadoEspacio.MANTENIMIENTO;
            default -> EnumEstadoEspacio.INACTIVO;
        };
    }
}
