package com.gestionatusalon.accesodatos;

import com.gestionatusalon.modelo.OrdenServicio;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrdenServicioDAO {

    public boolean crearOrden(int idReserva, OrdenServicio orden, String observacion) {
        String sql = """
                INSERT INTO Orden_Servicio (id_reserva, id_auxiliar, estado_orden, observacion)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            ps.setInt(2, orden.getIdAuxiliar());
            ps.setString(3, orden.getEstadoEntrega());
            ps.setString(4, observacion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al crear orden de servicio: " + e.getMessage());
            return false;
        }
    }

    public List<OrdenServicio> listarOrdenes() {
        List<OrdenServicio> ordenes = new ArrayList<>();

        String sql = """
                SELECT id_orden, id_auxiliar, estado_orden
                FROM Orden_Servicio
                ORDER BY id_orden
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OrdenServicio orden = new OrdenServicio();
                orden.setIdOrden(rs.getInt("id_orden"));
                orden.setIdAuxiliar(rs.getInt("id_auxiliar"));
                orden.setEstadoEntrega(rs.getString("estado_orden"));
                ordenes.add(orden);
            }

        } catch (Exception e) {
            System.out.println("Error al listar órdenes de servicio: " + e.getMessage());
        }

        return ordenes;
    }

    public boolean actualizarEstadoOrden(int idOrden, String nuevoEstado) {
        String sql = "UPDATE Orden_Servicio SET estado_orden = ? WHERE id_orden = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idOrden);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar estado de la orden: " + e.getMessage());
            return false;
        }
    }
}