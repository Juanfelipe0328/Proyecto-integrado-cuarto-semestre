package com.gestionatusalon.modelo;

public class Notificador {

    public void enviarEmail(String destinatario, String mensaje) {
        System.out.println("Correo enviado a: " + destinatario);
        System.out.println("Mensaje: " + mensaje);
    }

    public void enviarAlertaPush(int idUsuario, String titulo) {
        System.out.println("Alerta enviada al usuario con ID: " + idUsuario);
        System.out.println("Título: " + titulo);
    }
}