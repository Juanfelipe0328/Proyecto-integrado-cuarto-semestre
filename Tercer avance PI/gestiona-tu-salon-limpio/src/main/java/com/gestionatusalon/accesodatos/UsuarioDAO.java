package com.gestionatusalon.accesodatos;

import com.gestionatusalon.enumeraciones.EnumRol;
import com.gestionatusalon.modelo.Usuario;
import com.gestionatusalon.utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT u.id_usuario, u.nombre, u.apellido, u.correo, u.contrasena_hash, r.nombre_rol
                FROM Usuario u
                INNER JOIN Rol r ON u.id_rol = r.id_rol
                ORDER BY u.id_usuario
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre") + " " + rs.getString("apellido"));
                usuario.setEmail(rs.getString("correo"));
                usuario.setPasswordHash(rs.getString("contrasena_hash"));
                usuario.setRol(convertirRol(rs.getString("nombre_rol")));
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public Usuario autenticar(String correo, String clave) {
        Usuario usuario = null;

        String sql = """
                SELECT u.id_usuario, u.nombre, u.apellido, u.correo, u.contrasena_hash, r.nombre_rol
                FROM Usuario u
                INNER JOIN Rol r ON u.id_rol = r.id_rol
                WHERE u.correo = ? AND u.contrasena_hash = ? AND u.activo = true
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, clave);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre") + " " + rs.getString("apellido"));
                    usuario.setEmail(rs.getString("correo"));
                    usuario.setPasswordHash(rs.getString("contrasena_hash"));
                    usuario.setRol(convertirRol(rs.getString("nombre_rol")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar usuario: " + e.getMessage());
        }

        return usuario;
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo, String clave,
                                    String telefono, int idRol, Integer idPrograma) {

        String sql = """
                INSERT INTO Usuario (nombre, apellido, correo, contrasena_hash, telefono, activo, id_rol, id_programa)
                VALUES (?, ?, ?, ?, ?, true, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, correo);
            ps.setString(4, clave);
            ps.setString(5, telefono);
            ps.setInt(6, idRol);

            if (idPrograma != null) {
                ps.setInt(7, idPrograma);
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    private EnumRol convertirRol(String nombreRol) {
        if (nombreRol == null) {
            return EnumRol.ADMINISTRADOR;
        }

        return switch (nombreRol.toLowerCase()) {
            case "docente" -> EnumRol.DOCENTE;
            case "auxiliar logistica", "auxiliar logistico" -> EnumRol.AUXILIAR_LOGISTICO;
            default -> EnumRol.ADMINISTRADOR;
        };
    }
}
