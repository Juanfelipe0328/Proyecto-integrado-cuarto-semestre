package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.LogisticaControlador;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Incidente;
import com.gestionatusalon.modelo.OrdenServicio;
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

    private DefaultTableModel modeloIncidentes;
    private DefaultTableModel modeloOrdenes;
    private DefaultTableModel modeloReqs;
    private DefaultTableModel modeloEspacios;

    public MenuAuxiliarVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Auxiliar Logístico", PRIMARIO);
        add(crearHeader("Auxiliar Logístico", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"🏠", "Inicio",                "INICIO"},
            {"🏛", "Ver Espacios",          "ESPACIOS"},
            {"⚠",  "Ver Incidentes",        "INCIDENTES"},
            {"📝", "Registrar Incidente",   "REG_INCIDENTE"},
            {"📋", "Órdenes de Servicio",   "ORDENES"},
            {"➕", "Crear Orden",           "CREAR_ORDEN"},
            {"🔧", "Requerimientos",        "REQUERIMIENTOS"},
        };

        JPanel menu = crearMenuLateral("MENÚ AUXILIAR", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("🔧",
                "Gestiona incidentes, órdenes de servicio y requerimientos logísticos.", PRIMARIO, FONDO),
            construirEspacios(),
            construirVerIncidentes(),
            construirRegistrarIncidente(),
            construirOrdenes(),
            construirCrearOrden(),
            construirRequerimientos(),
        };

        construirLayout(menu,
            new String[]{"INICIO","ESPACIOS","INCIDENTES","REG_INCIDENTE","ORDENES","CREAR_ORDEN","REQUERIMIENTOS"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "INCIDENTES" -> cargarIncidentes();
            case "ORDENES"    -> cargarOrdenes();
            case "ESPACIOS"   -> cargarEspacios();
        }
    }

    // ── 1. VER ESPACIOS ────────────────────────────────────────
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID", "Nombre / Nomenclatura", "Capacidad", "Estado", "Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnCambiar = botonAccion("✏ Cambiar Estado", PRIMARIO);

        btnRef.addActionListener(e -> cargarEspacios());
        btnCambiar.addActionListener(e -> {
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

        JPanel btns = botonesPanel(btnRef, btnCambiar);
        cargarEspacios();
        return panelConTitulo("Espacios Físicos",
            envolverNorteSur(btns, new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 2. VER INCIDENTES ──────────────────────────────────────
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
            boolean ok = incidenteCtrl.cerrarIncidente(id);
            JOptionPane.showMessageDialog(this,
                ok ? "Incidente cerrado." : "No se pudo cerrar.",
                ok ? "Éxito" : "Error",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            cargarIncidentes();
        });

        cargarIncidentes();
        return panelConTitulo("Incidentes Reportados",
            envolverNorteSur(botonesPanel(btnRef, btnCerrar), new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 3. REGISTRAR INCIDENTE ─────────────────────────────────
    private JPanel construirRegistrarIncidente() {
        JPanel form = panelFormulario(BORDE);

        JTextArea txtDesc = new JTextArea(4, 25);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);
        JTextField txtSev     = campoTexto(); txtSev.setText("5");
        JTextField txtEspacio = campoTexto();
        JTextField txtEquipo  = campoTexto();

        int fila = 0;
        form.add(etiqueta("Descripción:"), gbcEtiqueta(fila));
        form.add(new JScrollPane(txtDesc), gbcCampo(fila++));
        form.add(etiqueta("Severidad (Baja=2 / Media=5 / Alta=8 / Crítica=10):"), gbcEtiqueta(fila));
        form.add(txtSev, gbcCampo(fila++));
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
                int sev     = Integer.parseInt(txtSev.getText().trim());
                Integer idE = txtEspacio.getText().isBlank() ? null : Integer.parseInt(txtEspacio.getText().trim());
                Integer idQ = txtEquipo.getText().isBlank()  ? null : Integer.parseInt(txtEquipo.getText().trim());
                boolean ok  = incidenteCtrl.registrarIncidente(usuarioActual.getIdUsuario(), desc, sev, idE, idQ);
                JOptionPane.showMessageDialog(this,
                    ok ? "Incidente registrado." : "No se pudo registrar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) { txtDesc.setText(""); txtSev.setText("5"); txtEspacio.setText(""); txtEquipo.setText(""); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Severidad e IDs deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false); wrap.add(form);
        return panelConTitulo("Registrar Nuevo Incidente", wrap, FONDO, PRIMARIO);
    }

    // ── 4. VER ÓRDENES ────────────────────────────────────────
    private JPanel construirOrdenes() {
        modeloOrdenes = crearModelo("ID Orden","ID Auxiliar","Estado");
        JTable tabla = crearTabla(modeloOrdenes, PRIMARIO);

        JButton btnRef  = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnAct  = botonAccion("✏ Actualizar Estado", PRIMARIO);

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
        return panelConTitulo("Órdenes de Servicio",
            envolverNorteSur(botonesPanel(btnRef, btnAct), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarOrdenes() {
        if (modeloOrdenes == null) return;
        modeloOrdenes.setRowCount(0);
        for (OrdenServicio o : logisticaCtrl.listarOrdenesServicio()) {
            modeloOrdenes.addRow(new Object[]{o.getIdOrden(), o.getIdAuxiliar(), o.getEstadoEntrega()});
        }
    }

    // ── 5. CREAR ORDEN ─────────────────────────────────────────
    private JPanel construirCrearOrden() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtIdReserva  = campoTexto();
        JTextField txtIdAuxiliar = campoTexto();
        txtIdAuxiliar.setText(String.valueOf(usuarioActual.getIdUsuario()));
        String[] estadosOp = {"Pendiente","En proceso","Completada"};
        JComboBox<String> cmbEstado = new JComboBox<>(estadosOp);
        cmbEstado.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JTextField txtObservacion = campoTexto();

        int fila = 0;
        form.add(etiqueta("ID Reserva:"),    gbcEtiqueta(fila)); form.add(txtIdReserva,  gbcCampo(fila++));
        form.add(etiqueta("ID Auxiliar:"),   gbcEtiqueta(fila)); form.add(txtIdAuxiliar, gbcCampo(fila++));
        form.add(etiqueta("Estado inicial:"),gbcEtiqueta(fila)); form.add(cmbEstado,      gbcCampo(fila++));
        form.add(etiqueta("Observación:"),   gbcEtiqueta(fila)); form.add(txtObservacion, gbcCampo(fila++));

        JButton btn = botonAccion("➕  Crear Orden", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            try {
                int idRes = Integer.parseInt(txtIdReserva.getText().trim());
                int idAux = Integer.parseInt(txtIdAuxiliar.getText().trim());
                String est = (String) cmbEstado.getSelectedItem();
                String obs = txtObservacion.getText().trim();
                boolean ok = logisticaCtrl.crearOrdenServicio(idRes, idAux, est, obs);
                JOptionPane.showMessageDialog(this,
                    ok ? "Orden creada correctamente." : "No se pudo crear la orden.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) { txtIdReserva.setText(""); txtObservacion.setText(""); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID Reserva e ID Auxiliar deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false); wrap.add(form);
        return panelConTitulo("Crear Orden de Servicio", wrap, FONDO, PRIMARIO);
    }

    // ── 6. VER / REGISTRAR REQUERIMIENTOS ─────────────────────
    private JPanel construirRequerimientos() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtIdReserva = campoTexto();
        JTextField txtSillas    = campoTexto(); txtSillas.setText("0");
        JTextField txtAC        = campoTexto(); txtAC.setText("20.0");
        JCheckBox  chkApoyo     = new JCheckBox("Sí requiero apoyo técnico");
        JTextField txtCategoria = campoTexto();

        int fila = 0;
        form.add(etiqueta("ID Reserva:"),                  gbcEtiqueta(fila)); form.add(txtIdReserva, gbcCampo(fila++));
        form.add(etiqueta("Sillas adicionales:"),           gbcEtiqueta(fila)); form.add(txtSillas,    gbcCampo(fila++));
        form.add(etiqueta("Temperatura AC (°C):"),          gbcEtiqueta(fila)); form.add(txtAC,        gbcCampo(fila++));
        form.add(etiqueta("Apoyo técnico:"),                gbcEtiqueta(fila)); form.add(chkApoyo,     gbcCampo(fila++));
        form.add(etiqueta("ID Categoría equipo (opcional):"),gbcEtiqueta(fila)); form.add(txtCategoria, gbcCampo(fila++));

        modeloReqs = crearModelo("ID Req","ID Reserva","Sillas","AC","Apoyo Técnico");
        JTable tablaReqs = crearTabla(modeloReqs, PRIMARIO);

        JButton btnGuardar   = botonAccion("💾  Guardar Requerimiento", PRIMARIO);
        JButton btnConsultar = botonAccion("🔍  Ver Requerimientos", PRIMARIO);

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
                    ok ? "Requerimiento registrado." : "No se pudo registrar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            } catch (NumberFormatException ex) { aviso("Ingrese un ID de reserva válido."); }
        });

        GridBagConstraints g = gbcBoton(fila);
        g.gridwidth = 1;
        form.add(btnGuardar,   g);
        g.gridx = 1;
        form.add(btnConsultar, g);

        JPanel lower = new JPanel(new BorderLayout());
        lower.setOpaque(false);
        lower.setBorder(new EmptyBorder(8, 0, 0, 0));
        lower.add(new JScrollPane(tablaReqs));

        JPanel todo = new JPanel(new BorderLayout(0, 8));
        todo.setOpaque(false);
        todo.add(form, BorderLayout.NORTH);
        todo.add(lower, BorderLayout.CENTER);

        return panelConTitulo("Requerimientos Logísticos por Reserva", todo, FONDO, PRIMARIO);
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

    private JPanel botonesPanel(JButton... botones) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setOpaque(false);
        for (JButton b : botones) p.add(b);
        return p;
    }
}
