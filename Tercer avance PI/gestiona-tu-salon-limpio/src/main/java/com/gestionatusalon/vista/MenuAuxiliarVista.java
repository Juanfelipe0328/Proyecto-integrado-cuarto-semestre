package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.LogisticaControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Incidente;
import com.gestionatusalon.modelo.OrdenServicio;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.RequerimientoLogistico;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuAuxiliarVista extends VentanaBase {

    private static final Color PRIMARIO = new Color(20, 110, 80);
    private static final Color MENU_BG  = new Color(25, 130, 95);
    private static final Color FONDO    = new Color(240, 250, 245);
    private static final Color BORDE    = new Color(180, 220, 200);

    private final IncidenteControlador incidenteCtrl = new IncidenteControlador();
    private final LogisticaControlador logisticaCtrl = new LogisticaControlador();
    private final EspacioControlador   espacioCtrl   = new EspacioControlador();
    private final ReservaControlador   reservaCtrl   = new ReservaControlador();

    private DefaultTableModel modeloIncidentes;
    private DefaultTableModel modeloOrdenes;
    private DefaultTableModel modeloReqs;
    private DefaultTableModel modeloEspacios;
    private DefaultTableModel modeloReservas;

    public MenuAuxiliarVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Auxiliar Logístico", PRIMARIO);
        add(crearHeader("Auxiliar Logístico", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"🏠", "Inicio",               "INICIO"},
            {"📅", "Todas las Reservas",   "RESERVAS"},
            {"🏛", "Ver Espacios",         "ESPACIOS"},
            {"⚠",  "Ver Incidentes",       "INCIDENTES"},
            {"📝", "Registrar Incidente",  "REG_INCIDENTE"},
            {"📋", "Órdenes de Servicio",  "ORDENES"},
            {"🔧", "Requerimientos",       "REQUERIMIENTOS"},
        };

        JPanel menu = crearMenuLateral("MENÚ AUXILIAR", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("🔧",
                "Gestiona reservas, incidentes, órdenes de servicio y requerimientos logísticos.", PRIMARIO, FONDO),
            construirReservas(),
            construirEspacios(),
            construirVerIncidentes(),
            construirRegistrarIncidente(),
            construirOrdenes(),
            construirRequerimientos(),
        };

        construirLayout(menu,
            new String[]{"INICIO","RESERVAS","ESPACIOS","INCIDENTES","REG_INCIDENTE","ORDENES","REQUERIMIENTOS"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "RESERVAS"        -> cargarReservas();
            case "INCIDENTES"      -> cargarIncidentes();
            case "ORDENES"         -> cargarOrdenes();
            case "ESPACIOS"        -> cargarEspacios();
            case "REQUERIMIENTOS"  -> cargarRequerimientos();
        }
    }

    // ── 1. TODAS LAS RESERVAS (solo lectura para el auxiliar) ──
    private JPanel construirReservas() {
        modeloReservas = crearModelo("ID Reserva","Usuario","Espacio","Fecha","Hora Ini","Hora Fin","Estado");
        JTable tabla = crearTabla(modeloReservas, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarReservas());
        cargarReservas();

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        info.setOpaque(false);
        info.add(btnRef);
        JLabel lbl = new JLabel("Consulta las reservas para organizar la preparación de cada espacio.");
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lbl.setForeground(Color.GRAY);
        info.add(lbl);

        return panelConTitulo("Todas las Reservas del Sistema",
            envolverNS(info, new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 2. VER ESPACIOS ────────────────────────────────────────
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID","Nombre / Espacio","Capacidad","Estado","Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);

        JButton btnRef    = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnEstado = botonAccion("✏ Cambiar Estado", PRIMARIO);

        btnRef.addActionListener(e -> cargarEspacios());
        btnEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione un espacio."); return; }
            int id = Integer.parseInt(modeloEspacios.getValueAt(fila, 0).toString());
            String[] estados = {"Disponible", "Mantenimiento", "Inactivo"};
            String nuevo = (String) JOptionPane.showInputDialog(this,
                "Nuevo estado:", "Cambiar Estado", JOptionPane.PLAIN_MESSAGE, null, estados, estados[0]);
            if (nuevo != null) {
                boolean ok = espacioCtrl.cambiarEstadoEspacio(id, nuevo);
                JOptionPane.showMessageDialog(this,
                    ok ? "Estado actualizado." : "No se pudo actualizar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarEspacios();
            }
        });

        cargarEspacios();
        return panelConTitulo("Espacios Físicos",
            envolverNS(botonesPanel(btnRef, btnEstado), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarEspacios() {
        if (modeloEspacios == null) return;
        modeloEspacios.setRowCount(0);
        for (EspacioFisico e : espacioCtrl.listarEspacios()) {
            modeloEspacios.addRow(new Object[]{
                e.getIdEspacio(), e.getNomenclatura(),
                e.getCapacidad(), e.getEstado(), e.getTipoNombre()
            });
        }
    }

    // ── 3. VER INCIDENTES ──────────────────────────────────────
    private JPanel construirVerIncidentes() {
        modeloIncidentes = crearModelo("ID","Descripción","Severidad","Prioridad","Fecha Reporte","Usuario");
        JTable tabla = crearTabla(modeloIncidentes, PRIMARIO);

        JButton btnRef    = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnCerrar = botonAccion("✅ Cerrar Incidente", PRIMARIO);

        btnRef.addActionListener(e -> cargarIncidentes());
        btnCerrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione un incidente."); return; }
            int id = (int) modeloIncidentes.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de cerrar este incidente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = incidenteCtrl.cerrarIncidente(id);
                JOptionPane.showMessageDialog(this,
                    ok ? "Incidente cerrado correctamente." : "No se pudo cerrar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarIncidentes();
            }
        });

        cargarIncidentes();
        return panelConTitulo("Incidentes Reportados",
            envolverNS(botonesPanel(btnRef, btnCerrar), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarIncidentes() {
        if (modeloIncidentes == null) return;
        modeloIncidentes.setRowCount(0);
        for (Incidente i : incidenteCtrl.listarIncidentes()) {
            modeloIncidentes.addRow(new Object[]{
                i.getIdIncidente(), i.getDescripcion(), i.getSeveridad(),
                i.asignarPrioridad(), i.getFechaReporte(), i.getIdUsuarioReporta()
            });
        }
    }

    // ── 4. REGISTRAR INCIDENTE ─────────────────────────────────
    private JPanel construirRegistrarIncidente() {
        JPanel form = panelFormulario(BORDE);

        JTextArea txtDesc = new JTextArea(4, 25);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);

        String[] sevNombres = {"Baja (2)", "Media (5)", "Alta (8)", "Crítica (10)"};
        int[]    sevValores = {2, 5, 8, 10};
        JComboBox<String> cmbSev = new JComboBox<>(sevNombres);
        cmbSev.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbSev.setSelectedIndex(1);

        JTextField txtEspacio = campoTexto();
        JTextField txtEquipo  = campoTexto();

        int fila = 0;
        form.add(etiqueta("Descripción:"), gbcEtiqueta(fila));
        form.add(new JScrollPane(txtDesc), gbcCampo(fila++));
        form.add(etiqueta("Severidad:"), gbcEtiqueta(fila));
        form.add(cmbSev, gbcCampo(fila++));
        form.add(etiqueta("ID Espacio (opcional):"), gbcEtiqueta(fila));
        form.add(txtEspacio, gbcCampo(fila++));
        form.add(etiqueta("ID Equipo (opcional):"), gbcEtiqueta(fila));
        form.add(txtEquipo, gbcCampo(fila++));

        JButton btn = botonAccion("⚠  Registrar Incidente", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            String desc = txtDesc.getText().trim();
            if (desc.isEmpty()) { aviso("La descripción es obligatoria."); return; }
            try {
                int sev     = sevValores[cmbSev.getSelectedIndex()];
                Integer idE = txtEspacio.getText().isBlank() ? null : Integer.parseInt(txtEspacio.getText().trim());
                Integer idQ = txtEquipo.getText().isBlank()  ? null : Integer.parseInt(txtEquipo.getText().trim());
                boolean ok  = incidenteCtrl.registrarIncidente(usuarioActual.getIdUsuario(), desc, sev, idE, idQ);
                JOptionPane.showMessageDialog(this,
                    ok ? "Incidente registrado." : "No se pudo registrar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) { txtDesc.setText(""); cmbSev.setSelectedIndex(1); txtEspacio.setText(""); txtEquipo.setText(""); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los IDs deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false); wrap.add(form);
        return panelConTitulo("Registrar Nuevo Incidente", wrap, FONDO, PRIMARIO);
    }

    // ── 5. ÓRDENES DE SERVICIO ─────────────────────────────────
    private JPanel construirOrdenes() {
        modeloOrdenes = crearModelo("ID Orden","ID Reserva","ID Auxiliar","Estado");
        JTable tabla = crearTabla(modeloOrdenes, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnAct = botonAccion("✏ Actualizar Estado", PRIMARIO);

        btnRef.addActionListener(e -> cargarOrdenes());
        btnAct.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una orden."); return; }
            int id = (int) modeloOrdenes.getValueAt(fila, 0);
            String[] estados = {"Pendiente","En proceso","Completada","Cancelada"};
            String nuevo = (String) JOptionPane.showInputDialog(this,
                "Nuevo estado:", "Actualizar Orden", JOptionPane.PLAIN_MESSAGE, null, estados, estados[0]);
            if (nuevo != null) {
                boolean ok = logisticaCtrl.actualizarEstadoOrden(id, nuevo);
                JOptionPane.showMessageDialog(this,
                    ok ? "Estado actualizado." : "No se pudo actualizar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarOrdenes();
            }
        });

        cargarOrdenes();

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        info.setOpaque(false);
        JLabel lbl = new JLabel("Cada orden representa una reserva que debe preparar. Actualice el estado conforme avance.");
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lbl.setForeground(Color.GRAY);
        info.add(lbl);

        JPanel norte = new JPanel(new BorderLayout(0,4));
        norte.setOpaque(false);
        norte.add(botonesPanel(btnRef, btnAct), BorderLayout.NORTH);
        norte.add(info, BorderLayout.SOUTH);

        return panelConTitulo("Órdenes de Servicio",
            envolverNS(norte, new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarOrdenes() {
        if (modeloOrdenes == null) return;
        modeloOrdenes.setRowCount(0);
        for (OrdenServicio o : logisticaCtrl.listarOrdenesServicio()) {
            modeloOrdenes.addRow(new Object[]{
                o.getIdOrden(), "—", o.getIdAuxiliar(), o.getEstadoEntrega()
            });
        }
    }

    // ── 6. REQUERIMIENTOS SOLICITADOS ──────────────────────────
    private JPanel construirRequerimientos() {
        modeloReqs = crearModelo("ID Req","ID Reserva","Sillas Adicionales","Temp. AC","Apoyo Técnico","Estado Prep.");
        JTable tabla = crearTabla(modeloReqs, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarRequerimientos());
        cargarRequerimientos();

        JLabel lbl = new JLabel("Lista de todos los requerimientos solicitados por los docentes para sus reservas.");
        lbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lbl.setForeground(Color.GRAY);

        JPanel norte = new JPanel(new BorderLayout(0, 4));
        norte.setOpaque(false);
        norte.add(botonesPanel(btnRef), BorderLayout.NORTH);
        norte.add(lbl, BorderLayout.SOUTH);

        return panelConTitulo("Requerimientos Logísticos Solicitados",
            envolverNS(norte, new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarRequerimientos() {
        if (modeloReqs == null) return;
        modeloReqs.setRowCount(0);
        for (RequerimientoLogistico r : logisticaCtrl.listarTodosRequerimientos()) {
            modeloReqs.addRow(new Object[]{
                r.getIdReq(),
                r.getIdReserva(),
                r.getSillasAdicionales(),
                r.getConfiguracionAC() + " °C",
                r.isRequiereApoyoTecnico() ? "Sí" : "No",
                "Pendiente"
            });
        }
    }

    // ── Helpers locales ────────────────────────────────────────
    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

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
