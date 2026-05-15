package com.gestionatusalon.vista;

import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Clase base con utilidades comunes para todas las ventanas de menú.
 * Evita repetir código de estilo en cada vista.
 */
public abstract class VentanaBase extends JFrame {

    protected final Usuario usuarioActual;
    protected JPanel panelContenido;
    protected CardLayout cardLayout;

    public VentanaBase(Usuario usuario, String titulo, Color colorPrimario) {
        this.usuarioActual = usuario;
        setTitle(titulo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(820, 560));
    }

    // ── Construcción del layout principal con menú lateral ─────
    protected void construirLayout(JPanel menuLateral, String[] panelIds, JPanel[] paneles) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);

        for (int i = 0; i < panelIds.length; i++) {
            panelContenido.add(paneles[i], panelIds[i]);
        }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuLateral, panelContenido);
        split.setDividerLocation(215);
        split.setDividerSize(2);
        split.setEnabled(false);
        add(split, BorderLayout.CENTER);
    }

    // ── Header común ───────────────────────────────────────────
    protected JPanel crearHeader(String rolTexto, Color colorFondo) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(colorFondo);
        header.setBorder(new EmptyBorder(11, 20, 11, 20));

        JLabel lblSistema = new JLabel("🏫  Gestiona Tu Salón  —  UNIAJC");
        lblSistema.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSistema.setForeground(Color.WHITE);

        JPanel derecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecho.setOpaque(false);

        JLabel lblUser = new JLabel("👤  " + usuarioActual.getNombre() + "  |  " + rolTexto);
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblUser.setForeground(new Color(210, 230, 255));

        JButton btnCerrar = new JButton("Cerrar sesión");
        btnCerrar.setBackground(new Color(180, 40, 40));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> cerrarSesion());

        derecho.add(lblUser);
        derecho.add(btnCerrar);
        header.add(lblSistema, BorderLayout.WEST);
        header.add(derecho, BorderLayout.EAST);
        return header;
    }

    // ── Menú lateral genérico ──────────────────────────────────
    protected JPanel crearMenuLateral(String titulo, String[][] opciones, Color colorMenu, Color colorHover) {
        JPanel panel = new JPanel();
        panel.setBackground(colorMenu);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lbl = new JLabel("  " + titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(180, 210, 255));
        lbl.setBorder(new EmptyBorder(0, 14, 10, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);

        for (String[] op : opciones) {
            // op[0]=icono, op[1]=texto, op[2]=card
            JButton btn = new JButton(op[0] + "  " + op[1]);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setBackground(colorMenu);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(new EmptyBorder(8, 16, 8, 10));
            btn.putClientProperty("card", op[2]);
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(colorHover); }
                public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(colorMenu);  }
            });
            btn.addActionListener(e -> {
                String card = (String) btn.getClientProperty("card");
                cardLayout.show(panelContenido, card);
                alCambiarPanel(card);
            });
            panel.add(btn);
            panel.add(Box.createVerticalStrut(2));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /** Sobrescribir en subclases para refrescar datos al cambiar panel */
    protected void alCambiarPanel(String card) {}

    // ── Cerrar sesión ──────────────────────────────────────────
    protected void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(this,
            "¿Desea cerrar sesión?", "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            new LoginVista().setVisible(true);
        }
    }

    // ── Helpers de tabla ───────────────────────────────────────
    protected DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    protected JTable crearTabla(DefaultTableModel modelo, Color colorHeader) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(colorHeader);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setShowGrid(true);
        return tabla;
    }

    // ── Helpers de formulario ──────────────────────────────────
    protected JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(55, 55, 55));
        return l;
    }

    protected JTextField campoTexto() {
        JTextField t = new JTextField();
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 190, 190)),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        return t;
    }

    protected JButton botonAccion(String texto, Color colorFondo) {
        JButton b = new JButton(texto);
        b.setBackground(colorFondo);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        return b;
    }

    protected JPanel panelBienvenida(String icono, String subtitulo, Color colorTexto, Color colorFondo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(colorFondo);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 230)),
            new EmptyBorder(40, 60, 40, 60)));

        JLabel ico = new JLabel(icono);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 48));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl1 = new JLabel("Bienvenido/a, " + usuarioActual.getNombre());
        lbl1.setFont(new Font("SansSerif", Font.BOLD, 19));
        lbl1.setForeground(colorTexto);
        lbl1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl2 = new JLabel(subtitulo);
        lbl2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl2.setForeground(Color.GRAY);
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(ico);
        card.add(Box.createVerticalStrut(14));
        card.add(lbl1);
        card.add(Box.createVerticalStrut(7));
        card.add(lbl2);

        panel.add(card);
        return panel;
    }

    /** Panel de formulario con borde y padding */
    protected JPanel panelFormulario(Color colorBorde) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorde),
            new EmptyBorder(22, 28, 22, 28)));
        return form;
    }

    protected GridBagConstraints gbcEtiqueta(int fila) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = fila; g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 6, 5, 10); g.weightx = 0;
        return g;
    }

    protected GridBagConstraints gbcCampo(int fila) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 1; g.gridy = fila; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 6); g.weightx = 1;
        return g;
    }

    protected GridBagConstraints gbcBoton(int fila) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = fila; g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(16, 6, 6, 6);
        return g;
    }

    /** Envuelve un panel en un scroll con título */
    protected JPanel panelConTitulo(String titulo, JComponent contenido, Color colorFondo, Color colorTitulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(colorFondo);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(colorTitulo);
        lbl.setBorder(new EmptyBorder(0, 0, 12, 0));

        p.add(lbl, BorderLayout.NORTH);
        p.add(contenido, BorderLayout.CENTER);
        return p;
    }
}