package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.UsuarioControlador;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginVista extends JFrame {

    private final UsuarioControlador usuarioControlador = new UsuarioControlador();

    // Campos login
    private JTextField txtCorreo;
    private JPasswordField txtClave;

    // Campos registro
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreoReg;
    private JPasswordField txtClaveReg;
    private JTextField txtTelefono;
    private JComboBox<String> cmbRol;

    private JPanel panelContenido;
    private CardLayout cardLayout;

    private static final Color AZUL = new Color(26, 60, 110);
    private static final Color AZUL_CLARO = new Color(240, 245, 255);

    public LoginVista() {
        setTitle("Gestiona Tu Salón - UNIAJC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 530);
        setLocationRelativeTo(null);
        setResizable(false);
        construirUI();
    }

    private void construirUI() {
        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(AZUL);

        // Encabezado
        JPanel encabezado = new JPanel();
        encabezado.setBackground(AZUL);
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setBorder(new EmptyBorder(22, 20, 14, 20));

        JLabel lblTitulo = new JLabel("🏫  Gestiona Tu Salón");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 21));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Institución Universitaria Antonio José Camacho");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setForeground(new Color(180, 210, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(lblSub);

        // CardLayout para alternar entre login y registro
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(Color.WHITE);
        panelContenido.add(panelLogin(), "LOGIN");
        panelContenido.add(panelRegistro(), "REGISTRO");

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(panelContenido, BorderLayout.CENTER);
        add(principal);
    }

    // ── PANEL LOGIN ────────────────────────────────────────────
    private JPanel panelLogin() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(18, 40, 18, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 0);
        g.gridwidth = 2;

        int fila = 0;

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setForeground(AZUL);
        g.gridy = fila++;
        p.add(lblTitulo, g);

        p.add(new JSeparator(), nuevaFila(g, fila++));

        p.add(etiqueta("Correo electrónico:"), nuevaFila(g, fila++));
        txtCorreo = new JTextField();
        campo(txtCorreo);
        p.add(txtCorreo, nuevaFila(g, fila++));

        p.add(etiqueta("Contraseña:"), nuevaFila(g, fila++));
        txtClave = new JPasswordField();
        campo(txtClave);
        p.add(txtClave, nuevaFila(g, fila++));

        g.gridy = fila++;
        g.insets = new Insets(14, 0, 5, 0);
        JButton btnIngresar = botonPrimario("Ingresar");
        btnIngresar.addActionListener(e -> accionLogin());
        txtClave.addActionListener(e -> accionLogin());
        p.add(btnIngresar, g);

        g.gridy = fila++;
        g.insets = new Insets(3, 0, 5, 0);
        JButton btnIr = enlace("¿No tienes cuenta? Regístrate aquí");
        btnIr.addActionListener(e -> cardLayout.show(panelContenido, "REGISTRO"));
        p.add(btnIr, g);

        return p;
    }

    // ── PANEL REGISTRO ─────────────────────────────────────────
    private JPanel panelRegistro() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(12, 40, 12, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 4, 0);
        g.gridwidth = 2;

        int fila = 0;

        JLabel lblTitulo = new JLabel("Crear Cuenta");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setForeground(AZUL);
        g.gridy = fila++;
        p.add(lblTitulo, g);

        p.add(new JSeparator(), nuevaFila(g, fila++));

        p.add(etiqueta("Nombre:"), nuevaFila(g, fila++));
        txtNombre = new JTextField(); campo(txtNombre);
        p.add(txtNombre, nuevaFila(g, fila++));

        p.add(etiqueta("Apellido:"), nuevaFila(g, fila++));
        txtApellido = new JTextField(); campo(txtApellido);
        p.add(txtApellido, nuevaFila(g, fila++));

        p.add(etiqueta("Correo electrónico:"), nuevaFila(g, fila++));
        txtCorreoReg = new JTextField(); campo(txtCorreoReg);
        p.add(txtCorreoReg, nuevaFila(g, fila++));

        p.add(etiqueta("Contraseña:"), nuevaFila(g, fila++));
        txtClaveReg = new JPasswordField(); campo(txtClaveReg);
        p.add(txtClaveReg, nuevaFila(g, fila++));

        p.add(etiqueta("Teléfono:"), nuevaFila(g, fila++));
        txtTelefono = new JTextField(); campo(txtTelefono);
        p.add(txtTelefono, nuevaFila(g, fila++));

        p.add(etiqueta("Rol:"), nuevaFila(g, fila++));
        cmbRol = new JComboBox<>(new String[]{"Docente", "Auxiliar Logística", "Coordinador"});
        cmbRol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        p.add(cmbRol, nuevaFila(g, fila++));

        g.gridy = fila++;
        g.insets = new Insets(12, 0, 4, 0);
        JButton btnReg = botonPrimario("Registrarme");
        btnReg.addActionListener(e -> accionRegistro());
        p.add(btnReg, g);

        g.gridy = fila++;
        g.insets = new Insets(3, 0, 4, 0);
        JButton btnIr = enlace("¿Ya tienes cuenta? Inicia sesión");
        btnIr.addActionListener(e -> cardLayout.show(panelContenido, "LOGIN"));
        p.add(btnIr, g);

        return p;
    }

    // ── ACCIONES ───────────────────────────────────────────────
    private void accionLogin() {
        String correo = txtCorreo.getText().trim();
        String clave  = new String(txtClave.getPassword()).trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese correo y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioControlador.iniciarSesion(correo, clave);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                "Correo o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
            return;
        }

        dispose();

        switch (usuario.getRol()) {
            case ADMINISTRADOR    -> new MenuAdministradorVista(usuario).setVisible(true);
            case AUXILIAR_LOGISTICO -> new MenuAuxiliarVista(usuario).setVisible(true);
            case DOCENTE          -> new MenuDocenteVista(usuario).setVisible(true);
            default -> {
                JOptionPane.showMessageDialog(null,
                    "Rol no reconocido en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                new LoginVista().setVisible(true);
            }
        }
    }

    private void accionRegistro() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo   = txtCorreoReg.getText().trim();
        String clave    = new String(txtClaveReg.getPassword()).trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nombre, apellido, correo y contraseña son obligatorios.",
                "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Docente=5, Auxiliar=3, Coordinador=2
        int[] idsRol = {5, 3, 2};
        int idRol = idsRol[cmbRol.getSelectedIndex()];

        boolean ok = usuarioControlador.registrarUsuario(
            nombre, apellido, correo, clave, telefono, idRol, null);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                "¡Cuenta creada! Ya puedes iniciar sesión.", "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            txtNombre.setText(""); txtApellido.setText(""); txtCorreoReg.setText("");
            txtClaveReg.setText(""); txtTelefono.setText("");
            cardLayout.show(panelContenido, "LOGIN");
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo registrar. El correo puede estar en uso.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── HELPERS VISUALES ───────────────────────────────────────
    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private void campo(JTextField t) {
        t.setPreferredSize(new Dimension(340, 32));
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 190, 190)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
    }

    private JButton botonPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(340, 36));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton enlace(String texto) {
        JButton b = new JButton(texto);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(AZUL);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints nuevaFila(GridBagConstraints g, int fila) {
        g.gridy = fila;
        g.insets = new Insets(4, 0, 4, 0);
        return g;
    }
}