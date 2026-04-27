package com.gestionatusalon.modelo;

import com.gestionatusalon.enumeraciones.EnumRol;

public class Usuario {

    protected int idUsuario;
    protected String nombre;
    protected String email;
    protected String passwordHash;
    protected EnumRol rol;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String email, String passwordHash, EnumRol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public boolean autenticar(String pass) {
        return this.passwordHash != null && this.passwordHash.equals(pass);
    }

    public void recuperarAcceso() {
        System.out.println("Proceso de recuperación enviado al correo: " + email);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public EnumRol getRol() {
        return rol;
    }

    public void setRol(EnumRol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
}