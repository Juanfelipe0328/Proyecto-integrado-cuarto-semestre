package com.gestionatusalon.accesodatos;

import com.gestionatusalon.modelo.RequerimientoLogistico;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RequerimientoLogisticoDAO {

    public boolean registrarRequerimiento(RequerimientoLogistico req, Integer idCategoria) {
        String sql = """
                INSERT INTO Detalle_Requerimiento
                (id_reserva, id_categoria, cantidad_solicitada, configuracion_especial, estado_preparacion)
                VALUES (?, ?, ?, ?, 'Pendiente')
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, req.getIdReserva());

            if (idCategoria != null) {
                ps.setInt(2, idCategoria);
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setInt(3, req.getSillasAdicionales());

            String configuracion = "AC: " + req.getConfiguracionAC() +
                    ", apoyoTecnico: " + req.isRequiereApoyoTecnico();

            ps.setString(4, configuracion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar requerimiento logístico: " + e.getMessage());
            return false;
        }
    }

    public List<RequerimientoLogistico> listarTodos() {
        List<RequerimientoLogistico> lista = new ArrayList<>();
        String sql = """
                SELECT id_detalle, id_reserva, cantidad_solicitada, configuracion_especial
                FROM Detalle_Requerimiento
                ORDER BY id_reserva, id_detalle
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RequerimientoLogistico req = new RequerimientoLogistico();
                req.setIdReq(rs.getInt("id_detalle"));
                req.setIdReserva(rs.getInt("id_reserva"));
                req.setSillasAdicionales(rs.getInt("cantidad_solicitada"));
                String config = rs.getString("configuracion_especial");
                req.setConfiguracionAC(extraerValorAC(config));
                req.setRequiereApoyoTecnico(config != null && config.toLowerCase().contains("apoyotecnico: true"));
                lista.add(req);
            }
        } catch (Exception e) {
            System.out.println("Error al listar todos los requerimientos: " + e.getMessage());
        }
        return lista;
    }

    public List<RequerimientoLogistico> listarPorReserva(int idReserva) {
        List<RequerimientoLogistico> lista = new ArrayList<>();

        String sql = """
                SELECT id_detalle, id_reserva, cantidad_solicitada, configuracion_especial
                FROM Detalle_Requerimiento
                WHERE id_reserva = ?
                ORDER BY id_detalle
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idReserva);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequerimientoLogistico req = new RequerimientoLogistico();
                    req.setIdReq(rs.getInt("id_detalle"));
                    req.setIdReserva(rs.getInt("id_reserva"));
                    req.setSillasAdicionales(rs.getInt("cantidad_solicitada"));

                    String config = rs.getString("configuracion_especial");
                    req.setConfiguracionAC(extraerValorAC(config));
                    req.setRequiereApoyoTecnico(config != null &&
                            config.toLowerCase().contains("apoyotecnico: true"));

                    lista.add(req);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al listar requerimientos por reserva: " + e.getMessage());
        }

        return lista;
    }

    private float extraerValorAC(String configuracion) {
        if (configuracion == null || !configuracion.contains("AC:")) {
            return 0;
        }

        try {
            String[] partes = configuracion.split(",");
            String valor = partes[0].replace("AC:", "").trim();
            return Float.parseFloat(valor);
        } catch (Exception e) {
            return 0;
        }
    }
}