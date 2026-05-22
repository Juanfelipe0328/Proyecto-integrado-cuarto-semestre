package com.gestionatusalon;

import com.gestionatusalon.vista.LoginVista;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Intentar look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si no funciona, usamos el default de Swing
        }

        // Lanzar la ventana de login en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            LoginVista login = new LoginVista();
            login.setVisible(true);
        });
    }
}
