package com.gestionatusalon.accesodatos;

import com.gestionatusalon.modelo.ItemEquipo;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemEquipoDAO {

    public List<ItemEquipo> listarEquipos() {
        List<ItemEquipo> equipos = new ArrayList<>();

        String sql = """
                SELECT serial, modelo, marca, estado_equipo, id_espacio
                FROM Equipo
                ORDER BY id_equipo
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ItemEquipo equipo = new ItemEquipo();
                equipo.setSerial(rs.getString("serial"));
                equipo.setNombre((rs.getString("marca") == null ? "" : rs.getString("marca")) + " " +
                                 (rs.getString("modelo") == null ? "" : rs.getString("modelo")));
                equipo.setIdEspacio(rs.getObject("id_espacio") != null
                        ? String.valueOf(rs.getInt("id_espacio")) : null);
                equipo.setEstadoFuncional(!"En mantenimiento".equalsIgnoreCase(rs.getString("estado_equipo")));
                equipos.add(equipo);
            }

        } catch (Exception e) {
            System.out.println("Error al listar equipos: " + e.getMessage());
        }

        return equipos;
    }

    public List<ItemEquipo> listarEquiposPorEspacio(int idEspacio) {
        List<ItemEquipo> equipos = new ArrayList<>();

        String sql = """
                SELECT serial, modelo, marca, estado_equipo, id_espacio
                FROM Equipo
                WHERE id_espacio = ?
                ORDER BY id_equipo
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEspacio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemEquipo equipo = new ItemEquipo();
                    equipo.setSerial(rs.getString("serial"));
                    equipo.setNombre((rs.getString("marca") == null ? "" : rs.getString("marca")) + " " +
                                     (rs.getString("modelo") == null ? "" : rs.getString("modelo")));
                    equipo.setIdEspacio(String.valueOf(rs.getInt("id_espacio")));
                    equipo.setEstadoFuncional(!"En mantenimiento".equalsIgnoreCase(rs.getString("estado_equipo")));
                    equipos.add(equipo);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar equipos por espacio: " + e.getMessage());
        }

        return equipos;
    }

    public boolean trasladarEquipoPorSerial(String serial, int nuevoIdEspacio) {
        String sql = "UPDATE Equipo SET id_espacio = ? WHERE serial = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, nuevoIdEspacio);
            ps.setString(2, serial);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al trasladar equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarMantenimiento(String serial) {
        String sql = "UPDATE Equipo SET estado_equipo = 'En mantenimiento' WHERE serial = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, serial);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar mantenimiento: " + e.getMessage());
            return false;
        }
    }
}