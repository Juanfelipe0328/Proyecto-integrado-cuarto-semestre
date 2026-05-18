package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.LogisticaControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.RequerimientoLogistico;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MenuDocenteVista extends VentanaBase {

    private static final Color PRIMARIO  = new Color(26, 60, 110);
    private static final Color MENU_BG   = new Color(35, 75, 135);
    private static final Color FONDO     = new Color(240, 245, 255);
    private static final Color BORDE     = new Color(200, 215, 240);

    private final EspacioControlador    espacioCtrl    = new EspacioControlador();
    private final ReservaControlador    reservaCtrl    = new ReservaControlador();
    private final IncidenteControlador  incidenteCtrl  = new IncidenteControlador();
    private final LogisticaControlador  logisticaCtrl  = new LogisticaControlador();

    // Modelos de tabla que necesitan recargarse
    private DefaultTableModel modeloEspacios;
    private DefaultTableModel modeloDisponibles;
    private DefaultTableModel modeloMisReservas;
    private DefaultTableModel modeloReqs;

    public MenuDocenteVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Docente", PRIMARIO);
        add(crearHeader("Docente", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"", "Inicio",                 "INICIO"},
            {"", "Ver Espacios",           "ESPACIOS"},
            {"", "Espacios Disponibles",   "DISPONIBLES"},
            {"", "Crear Reserva",          "CREAR_RESERVA"},
            {"", "Mis Reservas",           "MIS_RESERVAS"},
            {"", "Agregar Requerimiento",  "AGREGAR_REQ"},
            {"",  "Reportar Incidente",    "INCIDENTE"},
        };

        JPanel menu = crearMenuLateral("MENÚ DOCENTE", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("📚", "Consulta espacios, realiza reservas y reporta incidentes.", PRIMARIO, FONDO),
            construirEspacios(),
            construirDisponibles(),
            construirCrearReserva(),
            construirMisReservas(),
            construirAgregarRequerimiento(),
            construirReportarIncidente()
        };

        construirLayout(menu,
            new String[]{"INICIO","ESPACIOS","DISPONIBLES","CREAR_RESERVA","MIS_RESERVAS","AGREGAR_REQ","INCIDENTE"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "ESPACIOS"    -> cargarEspacios();
            case "DISPONIBLES" -> {} // el usuario pulsa Buscar
            case "MIS_RESERVAS"-> cargarMisReservas();
        }
    }

    // ── 1. VER TODOS LOS ESPACIOS ──────────────────────────────
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID", "Nombre / Nomenclatura", "Capacidad", "Estado", "Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);

        JButton btnRef = botonAccion(" Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarEspacios());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(new JLabel(""), BorderLayout.CENTER);
        norte.add(btnRef, BorderLayout.EAST);

        JPanel p = panelConTitulo("Espacios Físicos de la Institución",
            envolverNorteSur(norte, new JScrollPane(tabla)), FONDO, PRIMARIO);
        cargarEspacios();
        return p;
    }

    private void cargarEspacios() {
        if (modeloEspacios == null) return;
        modeloEspacios.setRowCount(0);
        for (EspacioFisico e : espacioCtrl.listarEspacios()) {
            modeloEspacios.addRow(new Object[]{
                e.getIdEspacio(), e.getNomenclatura(), e.getCapacidad(),
                e.getEstado(), e.getTipoNombre()
            });
        }
    }

    // ── 2. ESPACIOS DISPONIBLES ────────────────────────────────
    private JPanel construirDisponibles() {
        modeloDisponibles = crearModelo("ID", "Nombre / Nomenclatura", "Capacidad", "Estado", "Tipo");
        JTable tabla = crearTabla(modeloDisponibles, PRIMARIO);

        JTextField txtFecha = campoTexto();
        txtFecha.setText(LocalDate.now().toString());
        txtFecha.setPreferredSize(new Dimension(140, 30));

        JButton btnBuscar = botonAccion("Buscar", PRIMARIO);
        btnBuscar.addActionListener(e -> {
            try {
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                modeloDisponibles.setRowCount(0);
                List<EspacioFisico> lista = espacioCtrl.listarEspaciosDisponibles(fecha);
                if (lista.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "No hay espacios disponibles para " + fecha, "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                for (EspacioFisico esp : lista) {
                    modeloDisponibles.addRow(new Object[]{
                        esp.getIdEspacio(), esp.getNomenclatura(),
                        esp.getCapacidad(), esp.getEstado(), esp.getTipoNombre()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Fecha inválida. Use el formato AAAA-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtro.setOpaque(false);
        filtro.add(new JLabel("Fecha (AAAA-MM-DD):"));
        filtro.add(txtFecha);
        filtro.add(btnBuscar);

        JPanel p = panelConTitulo("Espacios Disponibles por Fecha",
            envolverNorteSur(filtro, new JScrollPane(tabla)), FONDO, PRIMARIO);
        return p;
    }

    // ── 3. CREAR RESERVA ───────────────────────────────────────
    private JPanel construirCrearReserva() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtIdEspacio  = campoTexto();
        JTextField txtFecha      = campoTexto(); txtFecha.setText(LocalDate.now().toString());
        JTextField txtHoraInicio = campoTexto(); txtHoraInicio.setText("07:00");
        JTextField txtHoraFin    = campoTexto(); txtHoraFin.setText("08:00");
        JTextField txtBloques    = campoTexto(); txtBloques.setText("1");
        JTextField txtObservacion= campoTexto();

        Object[][] campos = {
            {"ID del Espacio:", txtIdEspacio},
            {"Fecha (AAAA-MM-DD):", txtFecha},
            {"Hora inicio (HH:MM):", txtHoraInicio},
            {"Hora fin (HH:MM):", txtHoraFin},
            {"Bloques horarios (ej: 1,2,3):", txtBloques},
            {"Observación (opcional):", txtObservacion},
        };

        int fila = 0;
        for (Object[] c : campos) {
            form.add(etiqueta((String) c[0]), gbcEtiqueta(fila));
            form.add((Component) c[1], gbcCampo(fila));
            fila++;
        }

        JButton btn = botonAccion("  Crear Reserva", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            try {
                int idEsp = Integer.parseInt(txtIdEspacio.getText().trim());
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                LocalTime hi    = LocalTime.parse(txtHoraInicio.getText().trim());
                LocalTime hf    = LocalTime.parse(txtHoraFin.getText().trim());
                String obs      = txtObservacion.getText().trim();

                List<Integer> bloques = new ArrayList<>();
                for (String b : txtBloques.getText().split(",")) {
                    bloques.add(Integer.parseInt(b.trim()));
                }

                boolean ok = reservaCtrl.crearReserva(
                    usuarioActual.getIdUsuario(), String.valueOf(idEsp), fecha, hi, hf, bloques, obs);

                if (ok) {
                    JOptionPane.showMessageDialog(this,
                        " Reserva creada. Estado: Pendiente de aprobación.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    txtIdEspacio.setText(""); txtObservacion.setText(""); txtBloques.setText("1");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo crear la reserva.\nVerifique que el espacio y bloques estén disponibles.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(form);

        return panelConTitulo("Crear Nueva Reserva", wrap, FONDO, PRIMARIO);
    }

    // ── 4. MIS RESERVAS ────────────────────────────────────────
    private JPanel construirMisReservas() {
        modeloMisReservas = crearModelo("ID", "Espacio", "Fecha", "Hora Inicio", "Hora Fin", "Estado");
        JTable tabla = crearTabla(modeloMisReservas, PRIMARIO);

        JButton btnRef = botonAccion(" Refrescar", PRIMARIO);
        JButton btnCan = botonAccion(" Cancelar", new Color(180, 40, 40));

        btnRef.addActionListener(e -> cargarMisReservas());
        btnCan.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva de la lista."); return; }
            int id = (int) modeloMisReservas.getValueAt(fila, 0);
            String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:");
            if (motivo != null && !motivo.isBlank()) {
                boolean ok = reservaCtrl.cancelarReserva(id, motivo);
                JOptionPane.showMessageDialog(this,
                    ok ? "Reserva cancelada." : "No se pudo cancelar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarMisReservas();
            }
        });

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botonesPanel.setOpaque(false);
        botonesPanel.add(btnRef);
        botonesPanel.add(btnCan);

        cargarMisReservas();
        return panelConTitulo("Mis Reservas",
            envolverNorteSur(botonesPanel, new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarMisReservas() {
        if (modeloMisReservas == null) return;
        modeloMisReservas.setRowCount(0);
        for (Reserva r : reservaCtrl.listarReservasPorUsuario(usuarioActual.getIdUsuario())) {
            modeloMisReservas.addRow(new Object[]{
                r.getIdReserva(), r.getIdEspacio(), r.getFecha(),
                r.getHoraInicio(), r.getHoraFin(), r.getEstado()
            });
        }
    }

    // ── 5. AGREGAR REQUERIMIENTO LOGÍSTICO ─────────────────────
    private JPanel construirAgregarRequerimiento() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtIdReserva = campoTexto();
        JTextField txtSillas    = campoTexto(); txtSillas.setText("0");
        JTextField txtAC        = campoTexto(); txtAC.setText("20.0");
        JCheckBox  chkApoyo     = new JCheckBox("Sí requiero apoyo técnico");
        JTextField txtCategoria = campoTexto(); txtCategoria.setText("");

        int fila = 0;
        form.add(etiqueta("ID de la Reserva:"), gbcEtiqueta(fila));
        form.add(txtIdReserva, gbcCampo(fila++));
        form.add(etiqueta("Sillas adicionales:"), gbcEtiqueta(fila));
        form.add(txtSillas, gbcCampo(fila++));
        form.add(etiqueta("Temperatura AC (°C):"), gbcEtiqueta(fila));
        form.add(txtAC, gbcCampo(fila++));
        form.add(etiqueta("Apoyo técnico:"), gbcEtiqueta(fila));
        form.add(chkApoyo, gbcCampo(fila++));
        form.add(etiqueta("ID Categoría equipo (opcional):"), gbcEtiqueta(fila));
        form.add(txtCategoria, gbcCampo(fila++));

        // Panel para consultar requerimientos de una reserva
        modeloReqs = crearModelo("ID Req","ID Reserva","Sillas","AC","Apoyo Técnico");
        JTable tablaReqs = crearTabla(modeloReqs, PRIMARIO);

        JButton btnGuardar   = botonAccion("  Guardar Requerimiento", PRIMARIO);
        JButton btnConsultar = botonAccion("  Ver Requerimientos", PRIMARIO);

        btnGuardar.addActionListener(e -> {
            try {
                int idRes   = Integer.parseInt(txtIdReserva.getText().trim());
                int sillas  = Integer.parseInt(txtSillas.getText().trim());
                float ac    = Float.parseFloat(txtAC.getText().trim());
                boolean apo = chkApoyo.isSelected();
                Integer idCat = txtCategoria.getText().isBlank() ? null
                    : Integer.parseInt(txtCategoria.getText().trim());

                boolean ok = logisticaCtrl.registrarRequerimiento(idRes, sillas, ac, apo, idCat);
                JOptionPane.showMessageDialog(this,
                    ok ? "Requerimiento agregado correctamente." : "No se pudo agregar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnConsultar.addActionListener(e -> {
            try {
                int idRes = Integer.parseInt(txtIdReserva.getText().trim());
                modeloReqs.setRowCount(0);
                for (RequerimientoLogistico r : logisticaCtrl.listarRequerimientosPorReserva(idRes)) {
                    modeloReqs.addRow(new Object[]{
                        r.getIdReq(), r.getIdReserva(), r.getSillasAdicionales(),
                        r.getConfiguracionAC() + "°C", r.isRequiereApoyoTecnico() ? "Sí" : "No"
                    });
                }
            } catch (NumberFormatException ex) {
                aviso("Ingrese un ID de reserva válido.");
            }
        });

        GridBagConstraints gbcBtn = gbcBoton(fila);
        gbcBtn.gridwidth = 1;
        form.add(btnGuardar,   gbcBtn);
        gbcBtn.gridx = 1;
        form.add(btnConsultar, gbcBtn);

        JPanel lower = new JPanel(new BorderLayout());
        lower.setOpaque(false);
        lower.add(new JScrollPane(tablaReqs), BorderLayout.CENTER);

        JPanel todo = new JPanel(new BorderLayout(0, 10));
        todo.setOpaque(false);
        todo.add(form,  BorderLayout.NORTH);
        todo.add(lower, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(todo);

        return panelConTitulo("Agregar Requerimiento Logístico a una Reserva", wrap, FONDO, PRIMARIO);
    }

    // ── 6. REPORTAR INCIDENTE ──────────────────────────────────
    private JPanel construirReportarIncidente() {
        JPanel form = panelFormulario(BORDE);

        JTextArea txtDesc = new JTextArea(4, 25);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);
        JTextField txtSev    = campoTexto(); txtSev.setText("5");
        JTextField txtEspacio= campoTexto();
        JTextField txtEquipo = campoTexto();

        int fila = 0;
        form.add(etiqueta("Descripción:"), gbcEtiqueta(fila));
        form.add(new JScrollPane(txtDesc), gbcCampo(fila++));
        form.add(etiqueta("Severidad (Baja=2 / Media=5 / Alta=8 / Crítica=10):"), gbcEtiqueta(fila));
        form.add(txtSev, gbcCampo(fila++));
        form.add(etiqueta("ID Espacio (opcional):"), gbcEtiqueta(fila));
        form.add(txtEspacio, gbcCampo(fila++));
        form.add(etiqueta("ID Equipo (opcional):"), gbcEtiqueta(fila));
        form.add(txtEquipo, gbcCampo(fila++));

        JButton btn = botonAccion("⚠  Reportar Incidente", new Color(180, 80, 20));
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            String desc = txtDesc.getText().trim();
            if (desc.isEmpty()) { aviso("La descripción es obligatoria."); return; }
            try {
                int sev     = Integer.parseInt(txtSev.getText().trim());
                Integer idE = txtEspacio.getText().isBlank() ? null : Integer.parseInt(txtEspacio.getText().trim());
                Integer idQ = txtEquipo.getText().isBlank()  ? null : Integer.parseInt(txtEquipo.getText().trim());

                boolean ok = incidenteCtrl.registrarIncidente(usuarioActual.getIdUsuario(), desc, sev, idE, idQ);
                JOptionPane.showMessageDialog(this,
                    ok ? "Incidente reportado correctamente." : "No se pudo reportar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) { txtDesc.setText(""); txtSev.setText("5"); txtEspacio.setText(""); txtEquipo.setText(""); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Severidad e IDs deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(form);
        return panelConTitulo("Reportar Incidente Técnico", wrap, FONDO, PRIMARIO);
    }

    // ── Helpers locales ────────────────────────────────────────
    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel envolverNorteSur(Component norte, Component centro) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(norte, BorderLayout.NORTH);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }
}
