package com.gestionatusalon.controlador;

import com.gestionatusalon.accesodatos.UsuarioDAO;
import com.gestionatusalon.modelo.Usuario;

import java.util.List;

public class UsuarioControlador {

    private final UsuarioDAO usuarioDAO;

    public UsuarioControlador() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

    public Usuario iniciarSesion(String correo, String clave) {
        return usuarioDAO.autenticar(correo, clave);
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo,
                                    String clave, String telefono, int idRol, Integer idPrograma) {
        return usuarioDAO.registrarUsuario(nombre, apellido, correo, clave, telefono, idRol, idPrograma);
    }
}