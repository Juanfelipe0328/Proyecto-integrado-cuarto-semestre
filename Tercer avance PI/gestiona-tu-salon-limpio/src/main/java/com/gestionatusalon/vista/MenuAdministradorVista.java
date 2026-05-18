package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.*;
import com.gestionatusalon.modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MenuAdministradorVista extends VentanaBase {

    private static final Color PRIMARIO = new Color(100, 30, 120);
    private static final Color MENU_BG  = new Color(115, 40, 140);
    private static final Color FONDO    = new Color(248, 242, 255);
    private static final Color BORDE    = new Color(200, 180, 230);

    private final UsuarioControlador  usuarioCtrl   = new UsuarioControlador();
    private final EspacioControlador  espacioCtrl   = new EspacioControlador();
    private final ReservaControlador  reservaCtrl   = new ReservaControlador();
    private final IncidenteControlador incidenteCtrl = new IncidenteControlador();
    private final LogisticaControlador logisticaCtrl = new LogisticaControlador();
    private final ReporteControlador  reporteCtrl   = new ReporteControlador();

    private DefaultTableModel modeloUsuarios;
    private DefaultTableModel modeloEspacios;
    private DefaultTableModel modeloReservas;
    private DefaultTableModel modeloIncidentes;
    private DefaultTableModel modeloOrdenes;

    public MenuAdministradorVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Administrador", PRIMARIO);
        add(crearHeader("Administrador", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"", "Inicio",               "INICIO"},
            {"", "Usuarios",             "USUARIOS"},
            {"", "Registrar Usuario",    "REG_USUARIO"},
            {"", "Espacios Físicos",     "ESPACIOS"},
            {"", "Crear Espacio",        "CREAR_ESPACIO"},
            {"", "Todas las Reservas",   "RESERVAS"},
            {"",  "Incidentes",           "INCIDENTES"},
            {"", "Órdenes de Servicio",  "ORDENES"},
            {"", "Reportes y KPIs",      "REPORTES"},
        };

        JPanel menu = crearMenuLateral("MENÚ ADMINISTRADOR", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("⚙",
                "Control total del sistema: usuarios, espacios, reservas, incidentes y reportes.", PRIMARIO, FONDO),
            construirUsuarios(),
            construirRegistrarUsuario(),
            construirEspacios(),
            construirCrearEspacio(),
            construirReservas(),
            construirIncidentes(),
            construirOrdenes(),
            construirReportes(),
        };

        construirLayout(menu,
            new String[]{"INICIO","USUARIOS","REG_USUARIO","ESPACIOS","CREAR_ESPACIO","RESERVAS","INCIDENTES","ORDENES","REPORTES"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "USUARIOS"   -> cargarUsuarios();
            case "ESPACIOS"   -> cargarEspacios();
            case "RESERVAS"   -> cargarReservas();
            case "INCIDENTES" -> cargarIncidentes();
            case "ORDENES"    -> cargarOrdenes();
        }
    }

    // ── 1. LISTA DE USUARIOS ───────────────────────────────────
    private JPanel construirUsuarios() {
        modeloUsuarios = crearModelo("ID","Nombre Completo","Correo","Rol");
        JTable tabla = crearTabla(modeloUsuarios, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarUsuarios());

        cargarUsuarios();
        return panelConTitulo("Gestión de Usuarios",
            envolverNorteSur(botonesPanel(btnRef), new JScrollPane(tabla)), FONDO, PRIMARIO);
    }

    private void cargarUsuarios() {
        if (modeloUsuarios == null) return;
        modeloUsuarios.setRowCount(0);
        for (Usuario u : usuarioCtrl.obtenerUsuarios()) {
            modeloUsuarios.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getEmail(), u.getRol()});
        }
    }

    // ── 2. REGISTRAR USUARIO ───────────────────────────────────
    private JPanel construirRegistrarUsuario() {
        JPanel form = panelFormulario(BORDE);

        JTextField    txtNombre   = campoTexto();
        JTextField    txtApellido = campoTexto();
        JTextField    txtCorreo   = campoTexto();
        JPasswordField txtClave   = new JPasswordField();
        txtClave.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtClave.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190,190,190)),
            BorderFactory.createEmptyBorder(3,7,3,7)));
        JTextField    txtTelefono = campoTexto();

        String[] rolesNombres = {"Administrador","Coordinador","Auxiliar Logística","Directivo","Docente"};
        int[]    idsRol       = {1, 2, 3, 4, 5};
        JComboBox<String> cmbRol = new JComboBox<>(rolesNombres);
        cmbRol.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int fila = 0;
        form.add(etiqueta("Nombre:"),      gbcEtiqueta(fila)); form.add(txtNombre,   gbcCampo(fila++));
        form.add(etiqueta("Apellido:"),    gbcEtiqueta(fila)); form.add(txtApellido, gbcCampo(fila++));
        form.add(etiqueta("Correo:"),      gbcEtiqueta(fila)); form.add(txtCorreo,   gbcCampo(fila++));
        form.add(etiqueta("Contraseña:"),  gbcEtiqueta(fila)); form.add(txtClave,    gbcCampo(fila++));
        form.add(etiqueta("Teléfono:"),    gbcEtiqueta(fila)); form.add(txtTelefono, gbcCampo(fila++));
        form.add(etiqueta("Rol:"),         gbcEtiqueta(fila)); form.add(cmbRol,      gbcCampo(fila++));

        JButton btn = botonAccion("👤  Registrar Usuario", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            String nombre   = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String correo   = txtCorreo.getText().trim();
            String clave    = new String(txtClave.getPassword()).trim();
            String telefono = txtTelefono.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
                aviso("Nombre, apellido, correo y contraseña son obligatorios.");
                return;
            }

            int idRol = idsRol[cmbRol.getSelectedIndex()];
            boolean ok = usuarioCtrl.registrarUsuario(nombre, apellido, correo, clave, telefono, idRol, null);
            JOptionPane.showMessageDialog(this,
                ok ? "Usuario registrado correctamente." : "No se pudo registrar. El correo puede estar en uso.",
                ok ? "Éxito" : "Error",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (ok) {
                txtNombre.setText(""); txtApellido.setText(""); txtCorreo.setText("");
                txtClave.setText(""); txtTelefono.setText("");
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false); wrap.add(form);
        return panelConTitulo("Registrar Nuevo Usuario", wrap, FONDO, PRIMARIO);
    }

    // ── 3. ESPACIOS
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID","Nombre / Nomenclatura","Capacidad","Estado","Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);

        JButton btnRef    = botonAccion("🔄 Refrescar", PRIMARIO);
        JButton btnEstado = botonAccion("✏ Cambiar Estado", PRIMARIO);

        btnRef.addActionListener(e -> cargarEspacios());
        btnEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione un espacio."); return; }
            int id = Integer.parseInt(modeloEspacios.getValueAt(fila, 0).toString());
            String[] estados = {"Disponible","Mantenimiento","Inactivo"};
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
        return panelConTitulo("Espacios Físicos de la Institución",
            envolverNorteSur(botonesPanel(btnRef, btnEstado), new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // CREAR ESPACIO
    private JPanel construirCrearEspacio() {
        JPanel form = panelFormulario(BORDE);

        JTextField txtNombre    = campoTexto();
        JTextField txtBloque    = campoTexto();
        JTextField txtPiso      = campoTexto();  txtPiso.setText("1");
        JTextField txtCapacidad = campoTexto();  txtCapacidad.setText("30");
        JTextField txtIdSede    = campoTexto();  txtIdSede.setText("1");

        // Tipos de espacio según la BD: id_tipo_espacio
        // El admin debe saber los IDs, pero le damos una guía
        String[] tiposNombre = {"Salón (1)", "Laboratorio (2)", "Auditorio (3)", "Sala de Reuniones (4)"};
        int[]    tiposId     = {1, 2, 3, 4};
        JComboBox<String> cmbTipo = new JComboBox<>(tiposNombre);
        cmbTipo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        int fila = 0;
        form.add(etiqueta("Nombre del espacio:"),   gbcEtiqueta(fila)); form.add(txtNombre,    gbcCampo(fila++));
        form.add(etiqueta("Bloque:"),               gbcEtiqueta(fila)); form.add(txtBloque,    gbcCampo(fila++));
        form.add(etiqueta("Piso:"),                 gbcEtiqueta(fila)); form.add(txtPiso,      gbcCampo(fila++));
        form.add(etiqueta("Capacidad (personas):"), gbcEtiqueta(fila)); form.add(txtCapacidad, gbcCampo(fila++));
        form.add(etiqueta("ID Sede:"),              gbcEtiqueta(fila)); form.add(txtIdSede,    gbcCampo(fila++));
        form.add(etiqueta("Tipo de espacio:"),      gbcEtiqueta(fila)); form.add(cmbTipo,      gbcCampo(fila++));

        // Nota informativa
        JLabel nota = new JLabel("<html><small style='color:gray'> El espacio quedará en estado <b>Disponible</b> automáticamente.</small></html>");
        nota.setBorder(new EmptyBorder(4, 6, 0, 0));
        GridBagConstraints gNota = gbcEtiqueta(fila++);
        gNota.gridwidth = 2;
        form.add(nota, gNota);

        JButton btn = botonAccion("  Crear Espacio", PRIMARIO);
        form.add(btn, gbcBoton(fila));

        btn.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String bloque = txtBloque.getText().trim();
            String piso   = txtPiso.getText().trim();

            if (nombre.isEmpty() || bloque.isEmpty()) {
                aviso("El nombre y el bloque son obligatorios.");
                return;
            }

            try {
                int capacidad  = Integer.parseInt(txtCapacidad.getText().trim());
                int idSede     = Integer.parseInt(txtIdSede.getText().trim());
                int idTipo     = tiposId[cmbTipo.getSelectedIndex()];

                boolean ok = espacioCtrl.crearEspacio(nombre, bloque, piso, capacidad, idSede, idTipo);

                JOptionPane.showMessageDialog(this,
                    ok ? "✅ Espacio \"" + nombre + "\" creado correctamente.\nYa aparece en la lista de espacios."
                       : "No se pudo crear el espacio.\nVerifique que el ID de sede exista en la base de datos.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

                if (ok) {
                    txtNombre.setText(""); txtBloque.setText(""); txtPiso.setText("1");
                    txtCapacidad.setText("30");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Capacidad e ID Sede deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(form);
        return panelConTitulo("Crear Nuevo Espacio Físico", wrap, FONDO, PRIMARIO);
    }

    // ── 4. TODAS LAS RESERVAS ──────────────────────────────────
    private JPanel construirReservas() {
        modeloReservas = crearModelo("ID","Usuario","Espacio","Fecha","Hora Inicio","Hora Fin","Estado");
        JTable tabla = crearTabla(modeloReservas, PRIMARIO);

        JButton btnRef     = botonAccion(" Refrescar", PRIMARIO);
        JButton btnAprobar = botonAccion(" Aprobar", new Color(20, 130, 60));
        JButton btnCancelar= botonAccion(" Cancelar", new Color(180, 40, 40));

        btnRef.addActionListener(e -> cargarReservas());

        btnAprobar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva."); return; }
            int id = (int) modeloReservas.getValueAt(fila, 0);
            boolean ok = reservaCtrl.aprobarReserva(id);
            JOptionPane.showMessageDialog(this,
                ok ? "Reserva aprobada." : "No se pudo aprobar.",
                ok ? "Éxito" : "Error",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            cargarReservas();
        });

        btnCancelar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva."); return; }
            int id = (int) modeloReservas.getValueAt(fila, 0);
            String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:");
            if (motivo != null && !motivo.isBlank()) {
                boolean ok = reservaCtrl.cancelarReserva(id, motivo);
                JOptionPane.showMessageDialog(this,
                    ok ? "Reserva cancelada." : "No se pudo cancelar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarReservas();
            }
        });

        cargarReservas();
        return panelConTitulo("Todas las Reservas del Sistema",
            envolverNorteSur(botonesPanel(btnRef, btnAprobar, btnCancelar), new JScrollPane(tabla)),
            FONDO, PRIMARIO);
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

    // ── 5. INCIDENTES ──────────────────────────────────────────
    private JPanel construirIncidentes() {
        modeloIncidentes = crearModelo("ID","Descripción","Severidad","Prioridad","Fecha Reporte","Usuario");
        JTable tabla = crearTabla(modeloIncidentes, PRIMARIO);

        JButton btnRef    = botonAccion(" Refrescar", PRIMARIO);
        JButton btnCerrar = botonAccion(" Cerrar Incidente", PRIMARIO);

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

    // ── 6. ÓRDENES DE SERVICIO ─────────────────────────────────
    private JPanel construirOrdenes() {
        modeloOrdenes = crearModelo("ID Orden","ID Auxiliar","Estado");
        JTable tabla = crearTabla(modeloOrdenes, PRIMARIO);

        JButton btnRef = botonAccion(" Refrescar", PRIMARIO);
        JButton btnAct = botonAccion(" Actualizar Estado", PRIMARIO);

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

    // ── 7. REPORTES Y KPIs ─────────────────────────────────────
    private JPanel construirReportes() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Reportes y KPIs del Sistema");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(PRIMARIO);
        titulo.setBorder(new EmptyBorder(0,0,14,0));

        // Grid de tarjetas KPI
        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);

        // Tarjeta 1 - KPI de ocupación
        grid.add(crearTarjetaKPI(
            "  KPI de Ocupación",
            "Porcentaje de espacios con reservas registradas.",
            "Calcular KPI",
            () -> {
                double kpi = reporteCtrl.calcularKPIOcupacion();
                JOptionPane.showMessageDialog(this,
                    String.format("KPI de Ocupación: %.1f%%", kpi),
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            }
        ));

        // Tarjeta 2 - Espacios más solicitados
        grid.add(crearTarjetaKPI(
            "🏛  Espacios Más Solicitados",
            "Lista los espacios con más reservas en el sistema.",
            "Ver Lista",
            () -> {
                List<String> lista = reporteCtrl.obtenerEspaciosMasSolicitados();
                if (lista.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No hay datos registrados.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                StringBuilder sb = new StringBuilder("Espacios con reservas registradas:\n\n");
                for (String s : lista) sb.append("  •  Espacio ID: ").append(s).append("\n");
                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                area.setFont(new Font("SansSerif", Font.PLAIN, 13));
                JScrollPane scroll = new JScrollPane(area);
                scroll.setPreferredSize(new Dimension(340, 220));
                JOptionPane.showMessageDialog(this, scroll, "Espacios Solicitados", JOptionPane.INFORMATION_MESSAGE);
            }
        ));

        // Tarjeta 3 - Disponibilidad por fecha
        JPanel cardDisp = new JPanel();
        cardDisp.setLayout(new BoxLayout(cardDisp, BoxLayout.Y_AXIS));
        cardDisp.setBackground(Color.WHITE);
        cardDisp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(14,14,14,14)));

        JLabel lblCardDisp = new JLabel("  Disponibilidad por Fecha");
        lblCardDisp.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblCardDisp.setForeground(PRIMARIO);
        lblCardDisp.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descDisp = new JLabel("<html><small>Cuántos espacios están disponibles en una fecha.</small></html>");
        descDisp.setForeground(Color.GRAY);
        descDisp.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filaFecha.setOpaque(false);
        filaFecha.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtFechaDisp = new JTextField(LocalDate.now().toString(), 12);
        JButton btnDisp = botonAccion("Consultar", PRIMARIO);
        filaFecha.add(txtFechaDisp);
        filaFecha.add(btnDisp);

        btnDisp.addActionListener(e -> {
            try {
                LocalDate fecha = LocalDate.parse(txtFechaDisp.getText().trim());
                int total = espacioCtrl.listarEspaciosDisponibles(fecha).size();
                JOptionPane.showMessageDialog(this,
                    "Espacios disponibles el " + fecha + ": " + total,
                    "Resultado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Fecha inválida. Use AAAA-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cardDisp.add(lblCardDisp);
        cardDisp.add(Box.createVerticalStrut(6));
        cardDisp.add(descDisp);
        cardDisp.add(Box.createVerticalStrut(10));
        cardDisp.add(filaFecha);
        grid.add(cardDisp);

        // Tarjeta 4 - Generar reporte
        grid.add(crearTarjetaKPI(
            "  Generar Reporte",
            "Genera un reporte básico con datos actuales del sistema.",
            "Generar",
            () -> {
                String tipo = JOptionPane.showInputDialog(this, "Tipo de reporte (ej: Ocupación, Incidentes):");
                if (tipo != null && !tipo.isBlank()) {
                    ReporteGerencial r = reporteCtrl.generarReporteBasico(tipo);
                    String exportado   = reporteCtrl.exportarReportePDF();
                    JOptionPane.showMessageDialog(this,
                        "Reporte generado:\n" + r + "\n\n" + exportado,
                        "Reporte", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        ));

        p.add(titulo, BorderLayout.NORTH);
        p.add(grid,   BorderLayout.CENTER);
        return p;
    }

    private JPanel crearTarjetaKPI(String titulo, String descripcion, String textoBoton, Runnable accion) {
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

        JLabel desc = new JLabel("<html><small>" + descripcion + "</small></html>");
        desc.setForeground(Color.GRAY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn = botonAccion(textoBoton, PRIMARIO);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> accion.run());

        card.add(lbl);
        card.add(Box.createVerticalStrut(5));
        card.add(desc);
        card.add(Box.createVerticalStrut(10));
        card.add(btn);
        return card;
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
