package com.gestionatusalon.accesodatos;

import com.gestionatusalon.modelo.Incidente;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncidenteDAO {

    public List<Incidente> listarIncidentes() {
        List<Incidente> incidentes = new ArrayList<>();

        String sql = """
                SELECT id_incidente, descripcion, severidad, fecha_reporte, id_usuario
                FROM Incidente
                ORDER BY id_incidente
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Incidente incidente = new Incidente();
                incidente.setIdIncidente(rs.getInt("id_incidente"));
                incidente.setDescripcion(rs.getString("descripcion"));
                incidente.setSeveridad(convertirSeveridadEntera(rs.getString("severidad")));

                Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                incidente.setFechaReporte(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());

                incidente.setIdUsuarioReporta(rs.getInt("id_usuario"));
                incidentes.add(incidente);
            }

        } catch (Exception e) {
            System.out.println("Error al listar incidentes: " + e.getMessage());
        }

        return incidentes;
    }

    public boolean registrarIncidente(Incidente incidente, Integer idEspacio, Integer idEquipo) {
        String sql = """
                INSERT INTO Incidente (descripcion, severidad, id_usuario, id_espacio, id_equipo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, incidente.getDescripcion());
            ps.setString(2, convertirSeveridadTexto(incidente.getSeveridad()));
            ps.setInt(3, incidente.getIdUsuarioReporta());

            if (idEspacio != null) {
                ps.setInt(4, idEspacio);
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (idEquipo != null) {
                ps.setInt(5, idEquipo);
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar incidente: " + e.getMessage());
            return false;
        }
    }

    public boolean cerrarIncidente(int idIncidente) {
        String sql = "UPDATE Incidente SET estado_incidente = 'Solucionado' WHERE id_incidente = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idIncidente);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al cerrar incidente: " + e.getMessage());
            return false;
        }
    }

    private int convertirSeveridadEntera(String texto) {
        if (texto == null) return 5;

        return switch (texto.toLowerCase()) {
            case "baja" -> 2;
            case "media" -> 5;
            case "alta" -> 8;
            case "critica" -> 10;
            default -> 5;
        };
    }

    private String convertirSeveridadTexto(int severidad) {
        if (severidad >= 10) return "Critica";
        if (severidad >= 8) return "Alta";
        if (severidad >= 5) return "Media";
        return "Baja";
    }
}