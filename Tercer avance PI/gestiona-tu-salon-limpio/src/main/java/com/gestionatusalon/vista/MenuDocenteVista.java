package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.LogisticaControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuDocenteVista extends VentanaBase {

    private static final Color PRIMARIO = new Color(26, 60, 110);
    private static final Color MENU_BG  = new Color(35, 75, 135);
    private static final Color FONDO    = new Color(240, 245, 255);
    private static final Color BORDE    = new Color(200, 215, 240);

    private final EspacioControlador   espacioCtrl   = new EspacioControlador();
    private final ReservaControlador   reservaCtrl   = new ReservaControlador();
    private final IncidenteControlador incidenteCtrl = new IncidenteControlador();
    private final LogisticaControlador logisticaCtrl = new LogisticaControlador();

    // Bloques horarios fijos (igual que en la BD)
    private static final String[][] BLOQUES = {
        {"1","07:00","08:00"}, {"2","08:00","09:00"}, {"3","09:00","10:00"},
        {"4","10:00","11:00"}, {"5","11:00","12:00"}, {"6","12:00","13:00"},
        {"7","13:00","14:00"}, {"8","14:00","15:00"}, {"9","15:00","16:00"},
        {"10","16:00","17:00"},{"11","17:00","18:00"},{"12","18:00","19:00"},
        {"13","19:00","20:00"},{"14","20:00","21:00"},{"15","21:00","22:00"},
    };

    private DefaultTableModel modeloEspacios;
    private DefaultTableModel modeloDisponibles;
    private DefaultTableModel modeloMisReservas;

    public MenuDocenteVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Docente", PRIMARIO);
        add(crearHeader("Docente", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"🏠", "Inicio",               "INICIO"},
            {"🏛", "Ver Espacios",         "ESPACIOS"},
            {"✅", "Espacios Disponibles", "DISPONIBLES"},
            {"📅", "Crear Reserva",        "CREAR_RESERVA"},
            {"📋", "Mis Reservas",         "MIS_RESERVAS"},
            {"➕", "Agregar Requerimiento","AGREGAR_REQ"},
            {"⚠",  "Reportar Incidente",  "INCIDENTE"},
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
            case "ESPACIOS"     -> cargarEspacios();
            case "MIS_RESERVAS" -> cargarMisReservas();
        }
    }

    // ── 1. VER ESPACIOS ────────────────────────────────────────
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID", "Nombre / Espacio", "Capacidad", "Estado", "Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);
        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarEspacios());
        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(btnRef, BorderLayout.EAST);
        cargarEspacios();
        return panelConTitulo("Espacios Físicos de la Institución",
            envolverNorteSur(norte, new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 2. ESPACIOS DISPONIBLES ────────────────────────────────
    private JPanel construirDisponibles() {
        modeloDisponibles = crearModelo("ID", "Nombre / Espacio", "Capacidad", "Estado", "Tipo");
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
                    "Fecha inválida. Use AAAA-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtro.setOpaque(false);
        filtro.add(new JLabel("Fecha (AAAA-MM-DD):"));
        filtro.add(txtFecha);
        filtro.add(btnBuscar);

        return panelConTitulo("Espacios Disponibles por Fecha",
            envolverNorteSur(filtro, new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    // ── 3. CREAR RESERVA ───────────────────────────────────────
    private JPanel construirCrearReserva() {
        JPanel contenedor = new JPanel(new BorderLayout(14, 0));
        contenedor.setOpaque(false);

        // ── Formulario izquierdo ──
        JPanel form = panelFormulario(BORDE);

        // Combo de espacios
        JComboBox<String> cmbEspacio = new JComboBox<>();
        cmbEspacio.setFont(new Font("SansSerif", Font.PLAIN, 13));
        List<EspacioFisico> espacios = espacioCtrl.listarEspacios();
        for (EspacioFisico esp : espacios) {
            cmbEspacio.addItem(esp.getIdEspacio() + " — " + esp.getNomenclatura() + " (Cap: " + esp.getCapacidad() + ")");
        }

        JTextField txtFecha = campoTexto(); txtFecha.setText(LocalDate.now().toString());
        JLabel lblHoraInicio = new JLabel("—");
        lblHoraInicio.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblHoraInicio.setForeground(new Color(26, 60, 110));
        JLabel lblHoraFin = new JLabel("—");
        lblHoraFin.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblHoraFin.setForeground(new Color(26, 60, 110));
        JTextField txtObservacion = campoTexto();

        // Lista de selección de bloques
        DefaultListModel<String> modeloBloques = new DefaultListModel<>();
        for (String[] b : BLOQUES) {
            modeloBloques.addElement("Bloque " + b[0] + " — " + b[1] + " a " + b[2]);
        }
        JList<String> listaBloques = new JList<>(modeloBloques);
        listaBloques.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaBloques.setFont(new Font("SansSerif", Font.PLAIN, 12));
        listaBloques.setVisibleRowCount(6);

        // Al seleccionar bloques → calcular hora inicio y fin automáticamente
        listaBloques.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            List<Integer> seleccionados = Arrays.stream(listaBloques.getSelectedIndices())
            .boxed()
            .toList();
            if (seleccionados.isEmpty()) {
                lblHoraInicio.setText("—");
                lblHoraFin.setText("—");
            } else {
                int minIdx = seleccionados.stream().mapToInt(i -> i).min().getAsInt();
                int maxIdx = seleccionados.stream().mapToInt(i -> i).max().getAsInt();
                lblHoraInicio.setText(BLOQUES[minIdx][1]);
                lblHoraFin.setText(BLOQUES[maxIdx][2]);
            }
        });

        int fila = 0;
        form.add(etiqueta("Espacio:"), gbcEtiqueta(fila));
        form.add(cmbEspacio, gbcCampo(fila++));

        form.add(etiqueta("Fecha (AAAA-MM-DD):"), gbcEtiqueta(fila));
        form.add(txtFecha, gbcCampo(fila++));

        form.add(etiqueta("Bloques horarios:"), gbcEtiqueta(fila));
        form.add(new JScrollPane(listaBloques), gbcCampo(fila++));

        form.add(etiqueta("Hora inicio:"), gbcEtiqueta(fila));
        form.add(lblHoraInicio, gbcCampo(fila++));

        form.add(etiqueta("Hora fin:"), gbcEtiqueta(fila));
        form.add(lblHoraFin, gbcCampo(fila++));

        form.add(etiqueta("Observación:"), gbcEtiqueta(fila));
        form.add(txtObservacion, gbcCampo(fila++));

        JButton btn = botonAccion("📅  Crear Reserva", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            if (cmbEspacio.getItemCount() == 0) { aviso("No hay espacios disponibles."); return; }
            if (listaBloques.isSelectionEmpty()) { aviso("Seleccione al menos un bloque horario."); return; }
            if (lblHoraInicio.getText().equals("—")) { aviso("Seleccione los bloques horarios."); return; }

            try {
                // Extraer ID del espacio del combo
                String selEsp = (String) cmbEspacio.getSelectedItem();
                String idEspStr = selEsp.split("—")[0].trim();

                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                LocalTime hi = LocalTime.parse(lblHoraInicio.getText());
                LocalTime hf = LocalTime.parse(lblHoraFin.getText());
                String obs = txtObservacion.getText().trim();

                List<Integer> bloques = new ArrayList<>();
                for (int idx : listaBloques.getSelectedIndices()) {
                    bloques.add(Integer.parseInt(BLOQUES[idx][0]));
                }

                boolean ok = reservaCtrl.crearReserva(
                    usuarioActual.getIdUsuario(), idEspStr, fecha, hi, hf, bloques, obs);

                if (ok) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Reserva creada. Estado: Pendiente de aprobación.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    listaBloques.clearSelection();
                    txtObservacion.setText("");
                    lblHoraInicio.setText("—");
                    lblHoraFin.setText("—");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo crear la reserva.\nVerifique que los bloques seleccionados estén disponibles.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ── Tabla de referencia de bloques ──
        JPanel panelBloques = new JPanel(new BorderLayout(0, 6));
        panelBloques.setOpaque(false);
        panelBloques.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblRef = new JLabel("📋  Referencia de bloques horarios");
        lblRef.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRef.setForeground(PRIMARIO);

        DefaultTableModel modeloRef = new DefaultTableModel(new String[]{"Bloque", "Hora inicio", "Hora fin"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] b : BLOQUES) {
            modeloRef.addRow(new Object[]{"Bloque " + b[0], b[1], b[2]});
        }
        JTable tablaRef = crearTabla(modeloRef, PRIMARIO);
        tablaRef.setRowHeight(22);

        panelBloques.add(lblRef, BorderLayout.NORTH);
        panelBloques.add(new JScrollPane(tablaRef), BorderLayout.CENTER);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(form);

        contenedor.add(left, BorderLayout.CENTER);
        contenedor.add(panelBloques, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(contenedor);
        return panelConTitulo("Crear Nueva Reserva", wrap, FONDO, PRIMARIO);
    }

    // ── 4. MIS RESERVAS ────────────────────────────────────────
    private JPanel construirMisReservas() {
        modeloMisReservas = crearModelo("ID", "Espacio", "Fecha", "Hora Inicio", "Hora Fin", "Estado");
        JTable tabla = crearTabla(modeloMisReservas, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnCan = botonAccion("❌ Cancelar", new Color(180, 40, 40));

        btnRef.addActionListener(e -> cargarMisReservas());
        btnCan.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva de la lista."); return; }
            int id = (int) modeloMisReservas.getValueAt(fila, 0);
            String estado = (String) modeloMisReservas.getValueAt(fila, 5);
            if (estado.equals("Finalizada") || estado.equals("Cancelada")) {
                aviso("Solo se pueden cancelar reservas en estado Pendiente o Aprobada."); return;
            }
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

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setOpaque(false);
        btns.add(btnRef); btns.add(btnCan);

        cargarMisReservas();
        return panelConTitulo("Mis Reservas",
            envolverNorteSur(btns, new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 5. AGREGAR REQUERIMIENTO ───────────────────────────────
    private JPanel construirAgregarRequerimiento() {
        JPanel form = panelFormulario(BORDE);

        // Combo de reservas propias
        JComboBox<String> cmbReserva = new JComboBox<>();
        cmbReserva.setFont(new Font("SansSerif", Font.PLAIN, 13));
        List<Reserva> misReservas = reservaCtrl.listarReservasPorUsuario(usuarioActual.getIdUsuario());
        for (Reserva r : misReservas) {
            cmbReserva.addItem(r.getIdReserva() + " — " + r.getIdEspacio() + " — " + r.getFecha() + " — " + r.getEstado());
        }

        JTextField txtSillas = campoTexto(); txtSillas.setText("0");
        JTextField txtAC     = campoTexto(); txtAC.setText("20.0");
        JCheckBox  chkApoyo  = new JCheckBox("Sí requiero apoyo técnico");

        // Combo de categorías de equipo en lugar de ID
        String[] categorias = {"Ninguno", "1 — Videobeam", "2 — Laptop", "3 — Micrófono", "4 — Sonido", "5 — Pantalla"};
        JComboBox<String> cmbCategoria = new JComboBox<>(categorias);
        cmbCategoria.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int fila = 0;
        form.add(etiqueta("Reserva:"), gbcEtiqueta(fila));
        form.add(cmbReserva, gbcCampo(fila++));
        form.add(etiqueta("Sillas adicionales:"), gbcEtiqueta(fila));
        form.add(txtSillas, gbcCampo(fila++));
        form.add(etiqueta("Temperatura AC (°C):"), gbcEtiqueta(fila));
        form.add(txtAC, gbcCampo(fila++));
        form.add(etiqueta("Apoyo técnico:"), gbcEtiqueta(fila));
        form.add(chkApoyo, gbcCampo(fila++));
        form.add(etiqueta("Equipo adicional:"), gbcEtiqueta(fila));
        form.add(cmbCategoria, gbcCampo(fila++));

        JButton btn = botonAccion("➕  Guardar Requerimiento", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            if (cmbReserva.getItemCount() == 0) { aviso("No tiene reservas registradas."); return; }
            try {
                String selRes = (String) cmbReserva.getSelectedItem();
                int idRes  = Integer.parseInt(selRes.split("—")[0].trim());
                int sillas = Integer.parseInt(txtSillas.getText().trim());
                float ac   = Float.parseFloat(txtAC.getText().trim());
                boolean apo = chkApoyo.isSelected();

                // Extraer id de categoría del combo (0 = Ninguno)
                int idxCat = cmbCategoria.getSelectedIndex();
                Integer idCat = idxCat == 0 ? null : idxCat;

                boolean ok = logisticaCtrl.registrarRequerimiento(idRes, sillas, ac, apo, idCat);
                JOptionPane.showMessageDialog(this,
                    ok ? "✅ Requerimiento registrado correctamente." : "No se pudo registrar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) {
                    txtSillas.setText("0"); txtAC.setText("20.0");
                    chkApoyo.setSelected(false); cmbCategoria.setSelectedIndex(0);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Datos inválidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(form);
        return panelConTitulo("Agregar Requerimiento Logístico a mi Reserva", wrap, FONDO, PRIMARIO);
    }

    // ── 6. REPORTAR INCIDENTE ──────────────────────────────────
    private JPanel construirReportarIncidente() {
        JPanel form = panelFormulario(BORDE);

        JTextArea txtDesc = new JTextArea(4, 25);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);

        String[] severidades = {"Baja (2)", "Media (5)", "Alta (8)", "Crítica (10)"};
        int[]    valoresSev  = {2, 5, 8, 10};
        JComboBox<String> cmbSev = new JComboBox<>(severidades);
        cmbSev.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cmbSev.setSelectedIndex(1); // Media por defecto

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

        JButton btn = botonAccion("⚠  Reportar Incidente", new Color(180, 80, 20));
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            String desc = txtDesc.getText().trim();
            if (desc.isEmpty()) { aviso("La descripción es obligatoria."); return; }
            try {
                int sev     = valoresSev[cmbSev.getSelectedIndex()];
                Integer idE = txtEspacio.getText().isBlank() ? null : Integer.parseInt(txtEspacio.getText().trim());
                Integer idQ = txtEquipo.getText().isBlank()  ? null : Integer.parseInt(txtEquipo.getText().trim());
                boolean ok  = incidenteCtrl.registrarIncidente(usuarioActual.getIdUsuario(), desc, sev, idE, idQ);
                JOptionPane.showMessageDialog(this,
                    ok ? "Incidente reportado correctamente." : "No se pudo reportar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (ok) { txtDesc.setText(""); cmbSev.setSelectedIndex(1); txtEspacio.setText(""); txtEquipo.setText(""); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los IDs deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
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
