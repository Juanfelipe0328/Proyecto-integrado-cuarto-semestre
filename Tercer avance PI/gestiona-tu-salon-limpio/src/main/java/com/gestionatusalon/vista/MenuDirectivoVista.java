package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.ReporteControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.modelo.Incidente;
import com.gestionatusalon.modelo.ReporteGerencial;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MenuDirectivoVista extends VentanaBase {

    private static final Color PRIMARIO = new Color(160, 90, 10);
    private static final Color MENU_BG  = new Color(185, 110, 20);
    private static final Color FONDO    = new Color(255, 250, 240);
    private static final Color BORDE    = new Color(230, 190, 130);

    private final ReporteControlador   reporteCtrl   = new ReporteControlador();
    private final ReservaControlador   reservaCtrl   = new ReservaControlador();
    private final IncidenteControlador incidenteCtrl = new IncidenteControlador();
    private final EspacioControlador   espacioCtrl   = new EspacioControlador();

    private DefaultTableModel modeloReservas;
    private DefaultTableModel modeloIncidentes;

    public MenuDirectivoVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Directivo", PRIMARIO);
        add(crearHeader("Directivo", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"🏠", "Inicio",              "INICIO"},
            {"📊", "KPIs de Ocupación",   "KPIS"},
            {"📅", "Consultar Reservas",  "RESERVAS"},
            {"⚠",  "Ver Incidentes",     "INCIDENTES"},
            {"📄", "Generar Reporte",     "REPORTE"},
        };

        JPanel menu = crearMenuLateral("MENÚ DIRECTIVO", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("📊",
                "Consulte indicadores de ocupación y estadísticas institucionales.", PRIMARIO, FONDO),
            construirKPIs(),
            construirReservas(),
            construirIncidentes(),
            construirGenerarReporte(),
        };

        construirLayout(menu,
            new String[]{"INICIO","KPIS","RESERVAS","INCIDENTES","REPORTE"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "RESERVAS"   -> cargarReservas();
            case "INCIDENTES" -> cargarIncidentes();
        }
    }

    // ── 1. KPIs ────────────────────────────────────────────────
    private JPanel construirKPIs() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Indicadores de Ocupación");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(PRIMARIO);
        titulo.setBorder(new EmptyBorder(0,0,14,0));

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        // KPI 1 — Ocupación global
        JPanel c1 = crearCard("📊  Ocupación Global");
        JLabel lblKpi = new JLabel("—");
        lblKpi.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblKpi.setForeground(PRIMARIO);
        lblKpi.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btn1 = botonAccion("Calcular", PRIMARIO);
        btn1.addActionListener(e -> {
            double kpi = reporteCtrl.calcularKPIOcupacion();
            lblKpi.setText(String.format("%.1f%%", kpi));
        });
        c1.add(lblKpi);
        c1.add(Box.createVerticalStrut(6));
        c1.add(btn1);
        grid.add(c1);

        // KPI 2 — Total reservas
        JPanel c2 = crearCard("📅  Total de Reservas");
        JLabel lblTotal = new JLabel("—");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTotal.setForeground(PRIMARIO);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btn2 = botonAccion("Contar", PRIMARIO);
        btn2.addActionListener(e -> {
            int total = reservaCtrl.listarReservas().size();
            lblTotal.setText(String.valueOf(total));
        });
        c2.add(lblTotal);
        c2.add(Box.createVerticalStrut(6));
        c2.add(btn2);
        grid.add(c2);

        // KPI 3 — Incidentes activos
        JPanel c3 = crearCard("⚠  Incidentes Reportados");
        JLabel lblInc = new JLabel("—");
        lblInc.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblInc.setForeground(new Color(180, 50, 20));
        lblInc.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btn3 = botonAccion("Contar", PRIMARIO);
        btn3.addActionListener(e -> {
            int total = incidenteCtrl.listarIncidentes().size();
            lblInc.setText(String.valueOf(total));
        });
        c3.add(lblInc);
        c3.add(Box.createVerticalStrut(6));
        c3.add(btn3);
        grid.add(c3);

        // KPI 4 — Disponibilidad por fecha
        JPanel c4 = crearCard("🏛  Disponibilidad por Fecha");
        JTextField txtF = new JTextField(LocalDate.now().toString(), 12);
        txtF.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblDisp = new JLabel("—  espacios libres");
        lblDisp.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDisp.setForeground(PRIMARIO);
        lblDisp.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btn4 = botonAccion("Consultar", PRIMARIO);
        btn4.addActionListener(e -> {
            try {
                LocalDate fecha = LocalDate.parse(txtF.getText().trim());
                int n = espacioCtrl.listarEspaciosDisponibles(fecha).size();
                lblDisp.setText(n + "  espacios libres");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fecha inválida. Use AAAA-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        c4.add(txtF);
        c4.add(Box.createVerticalStrut(5));
        c4.add(lblDisp);
        c4.add(Box.createVerticalStrut(6));
        c4.add(btn4);
        grid.add(c4);

        p.add(titulo, BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearCard(String titulo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(14, 14, 14, 14)));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(PRIMARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        return card;
    }

    // ── 2. RESERVAS ────────────────────────────────────────────
    private JPanel construirReservas() {
        modeloReservas = crearModelo("ID","Usuario","Espacio","Fecha","Hora Ini","Hora Fin","Estado");
        JTable tabla = crearTabla(modeloReservas, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarReservas());

        cargarReservas();
        return panelConTitulo("Consulta de Reservas (Solo Lectura)",
            envolverNS(botonesPanel(btnRef), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarReservas() {
        if (modeloReservas == null) return;
        modeloReservas.setRowCount(0);
        for (Reserva r : reservaCtrl.listarReservas()) {
            modeloReservas.addRow(new Object[]{
                r.getIdReserva(), r.getIdDocente(), r.getIdEspacio(),
                r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getEstado()
            });
        }
    }

    // ── 3. INCIDENTES ──────────────────────────────────────────
    private JPanel construirIncidentes() {
        modeloIncidentes = crearModelo("ID","Descripción","Severidad","Prioridad","Fecha Reporte");
        JTable tabla = crearTabla(modeloIncidentes, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarIncidentes());

        cargarIncidentes();
        return panelConTitulo("Incidentes Reportados (Solo Lectura)",
            envolverNS(botonesPanel(btnRef), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarIncidentes() {
        if (modeloIncidentes == null) return;
        modeloIncidentes.setRowCount(0);
        for (Incidente i : incidenteCtrl.listarIncidentes()) {
            modeloIncidentes.addRow(new Object[]{
                i.getIdIncidente(), i.getDescripcion(),
                i.getSeveridad(), i.asignarPrioridad(), i.getFechaReporte()
            });
        }
    }

    // ── 4. GENERAR REPORTE ─────────────────────────────────────
    private JPanel construirGenerarReporte() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtTipo = campoTexto();
        txtTipo.setText("Ocupación");

        int fila = 0;
        form.add(etiqueta("Tipo de reporte:"), gbcEtiqueta(fila));
        form.add(txtTipo, gbcCampo(fila++));

        JTextArea txtResultado = new JTextArea(6, 30);
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtResultado.setLineWrap(true);

        JButton btn = botonAccion("📄  Generar Reporte", PRIMARIO);
        form.add(btn, gbcBoton(fila++));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = fila; g.gridwidth = 2;
        g.fill = GridBagConstraints.BOTH; g.weightx = 1; g.weighty = 1;
        g.insets = new Insets(8, 6, 6, 6);
        form.add(new JScrollPane(txtResultado), g);

        btn.addActionListener(e -> {
            String tipo = txtTipo.getText().trim();
            if (tipo.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese un tipo de reporte.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
            ReporteGerencial r = reporteCtrl.generarReporteBasico(tipo);
            String exportado   = reporteCtrl.exportarReportePDF();
            double kpi         = reporteCtrl.calcularKPIOcupacion();
            List<String> esps  = reporteCtrl.obtenerEspaciosMasSolicitados();
            txtResultado.setText(
                "=== REPORTE: " + tipo.toUpperCase() + " ===\n" +
                "Fecha: " + r.getFechaGeneracion() + "\n\n" +
                "KPI de Ocupación: " + String.format("%.1f%%", kpi) + "\n" +
                "Total reservas registradas: " + esps.size() + "\n" +
                "Total incidentes: " + incidenteCtrl.listarIncidentes().size() + "\n\n" +
                exportado
            );
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false); wrap.add(form);
        return panelConTitulo("Generar Reporte Gerencial", wrap, FONDO, PRIMARIO);
    }

    // ── Helpers locales ────────────────────────────────────────
    private JPanel envolverNS(Component norte, Component centro) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(norte, BorderLayout.NORTH);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel botonesPanel(JButton... botones) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setOpaque(false);
        for (JButton b : botones) p.add(b);
        return p;
    }
}