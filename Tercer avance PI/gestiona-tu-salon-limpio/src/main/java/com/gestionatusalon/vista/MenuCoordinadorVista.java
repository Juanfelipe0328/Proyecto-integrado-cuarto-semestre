package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MenuCoordinadorVista extends VentanaBase {

    private static final Color PRIMARIO = new Color(15, 90, 160);
    private static final Color MENU_BG  = new Color(20, 110, 185);
    private static final Color FONDO    = new Color(238, 246, 255);
    private static final Color BORDE    = new Color(170, 210, 250);

    private final ReservaControlador  reservaCtrl = new ReservaControlador();
    private final EspacioControlador  espacioCtrl = new EspacioControlador();

    private DefaultTableModel modeloReservas;
    private DefaultTableModel modeloEspacios;
    private DefaultTableModel modeloDisponibles;

    public MenuCoordinadorVista(Usuario usuario) {
        super(usuario, "Gestiona Tu Salón — Coordinador", PRIMARIO);
        add(crearHeader("Coordinador", PRIMARIO), BorderLayout.NORTH);

        String[][] opciones = {
            {"🏠", "Inicio",                "INICIO"},
            {"📅", "Todas las Reservas",    "RESERVAS"},
            {"✅", "Aprobar / Cancelar",    "GESTIONAR"},
            {"🏛", "Ver Espacios",          "ESPACIOS"},
            {"📆", "Disponibilidad",        "DISPONIBLES"},
        };

        JPanel menu = crearMenuLateral("MENÚ COORDINADOR", opciones, MENU_BG, PRIMARIO);

        JPanel[] paneles = {
            panelBienvenida("📋",
                "Aprueba reservas, consulta espacios y supervisa la disponibilidad.", PRIMARIO, FONDO),
            construirReservas(),
            construirGestionarReservas(),
            construirEspacios(),
            construirDisponibles(),
        };

        construirLayout(menu,
            new String[]{"INICIO","RESERVAS","GESTIONAR","ESPACIOS","DISPONIBLES"},
            paneles);
    }

    @Override
    protected void alCambiarPanel(String card) {
        switch (card) {
            case "RESERVAS"  -> cargarReservas();
            case "GESTIONAR" -> cargarReservasGestion();
            case "ESPACIOS"  -> cargarEspacios();
        }
    }

    // ── 1. VER TODAS LAS RESERVAS ──────────────────────────────
    private JPanel construirReservas() {
        modeloReservas = crearModelo("ID","Usuario","Espacio","Fecha","Hora Ini","Hora Fin","Estado");
        JTable tabla = crearTabla(modeloReservas, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarReservas());

        cargarReservas();
        return panelConTitulo("Todas las Reservas del Sistema",
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

    // ── 2. APROBAR / CANCELAR RESERVAS ─────────────────────────
    private DefaultTableModel modeloGestion;

    private JPanel construirGestionarReservas() {
        modeloGestion = crearModelo("ID","Usuario","Espacio","Fecha","Hora Ini","Hora Fin","Estado");
        JTable tabla = crearTabla(modeloGestion, PRIMARIO);

        JButton btnRef     = botonAccion("🔄 Refrescar",  PRIMARIO);
        JButton btnAprobar = botonAccion("✅ Aprobar",    new Color(20, 130, 60));
        JButton btnCancelar= botonAccion("❌ Cancelar",   new Color(180, 40, 40));

        btnRef.addActionListener(e -> cargarReservasGestion());

        btnAprobar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva."); return; }
            int id = (int) modeloGestion.getValueAt(fila, 0);
            boolean ok = reservaCtrl.aprobarReserva(id);
            JOptionPane.showMessageDialog(this,
                ok ? "✅ Reserva aprobada correctamente." : "No se pudo aprobar la reserva.",
                ok ? "Éxito" : "Error",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            cargarReservasGestion();
        });

        btnCancelar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Seleccione una reserva."); return; }
            int id = (int) modeloGestion.getValueAt(fila, 0);
            String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:");
            if (motivo != null && !motivo.isBlank()) {
                boolean ok = reservaCtrl.cancelarReserva(id, motivo);
                JOptionPane.showMessageDialog(this,
                    ok ? "Reserva cancelada." : "No se pudo cancelar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                cargarReservasGestion();
            }
        });

        cargarReservasGestion();
        return panelConTitulo("Aprobar o Cancelar Reservas",
            envolverNS(botonesPanel(btnRef, btnAprobar, btnCancelar), new JScrollPane(tabla)),
            FONDO, PRIMARIO);
    }

    private void cargarReservasGestion() {
        if (modeloGestion == null) return;
        modeloGestion.setRowCount(0);
        for (Reserva r : reservaCtrl.listarReservas()) {
            modeloGestion.addRow(new Object[]{
                r.getIdReserva(), r.getIdDocente(), r.getIdEspacio(),
                r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getEstado()
            });
        }
    }

    // ── 3. VER ESPACIOS ────────────────────────────────────────
    private JPanel construirEspacios() {
        modeloEspacios = crearModelo("ID","Nombre / Nomenclatura","Capacidad","Estado","Tipo");
        JTable tabla = crearTabla(modeloEspacios, PRIMARIO);

        JButton btnRef = botonAccion("🔄 Refrescar", PRIMARIO);
        btnRef.addActionListener(e -> cargarEspacios());

        cargarEspacios();
        return panelConTitulo("Espacios Físicos",
            envolverNS(botonesPanel(btnRef), new JScrollPane(tabla)), FONDO, PRIMARIO);
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

    // ── 4. DISPONIBILIDAD POR FECHA ────────────────────────────
    private JPanel construirDisponibles() {
        modeloDisponibles = crearModelo("ID","Nombre / Nomenclatura","Capacidad","Estado");
        JTable tabla = crearTabla(modeloDisponibles, PRIMARIO);

        JTextField txtFecha = campoTexto();
        txtFecha.setText(LocalDate.now().toString());
        txtFecha.setPreferredSize(new Dimension(140, 30));

        JButton btnBuscar = botonAccion("🔍 Buscar", PRIMARIO);
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
                        esp.getIdEspacio(), esp.getNomenclatura(), esp.getCapacidad(), esp.getEstado()
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

        return panelConTitulo("Disponibilidad de Espacios por Fecha",
            envolverNS(filtro, new JScrollPane(tabla)), FONDO, PRIMARIO);
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
