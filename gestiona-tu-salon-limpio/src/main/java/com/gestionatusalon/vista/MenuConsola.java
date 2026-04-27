package com.gestionatusalon.vista;

import com.gestionatusalon.controlador.EspacioControlador;
import com.gestionatusalon.controlador.IncidenteControlador;
import com.gestionatusalon.controlador.LogisticaControlador;
import com.gestionatusalon.controlador.ReporteControlador;
import com.gestionatusalon.controlador.ReservaControlador;
import com.gestionatusalon.controlador.UsuarioControlador;
import com.gestionatusalon.enumeraciones.EnumRol;
import com.gestionatusalon.modelo.EspacioFisico;
import com.gestionatusalon.modelo.Incidente;
import com.gestionatusalon.modelo.OrdenServicio;
import com.gestionatusalon.modelo.ReporteGerencial;
import com.gestionatusalon.modelo.Reserva;
import com.gestionatusalon.modelo.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuConsola {

    private final Scanner scanner;
    private final UsuarioControlador usuarioControlador;
    private final EspacioControlador espacioControlador;
    private final ReservaControlador reservaControlador;
    private final IncidenteControlador incidenteControlador;
    private final LogisticaControlador logisticaControlador;
    private final ReporteControlador reporteControlador;

    private Usuario usuarioActual;

    public MenuConsola() {
        this.scanner = new Scanner(System.in);
        this.usuarioControlador = new UsuarioControlador();
        this.espacioControlador = new EspacioControlador();
        this.reservaControlador = new ReservaControlador();
        this.incidenteControlador = new IncidenteControlador();
        this.logisticaControlador = new LogisticaControlador();
        this.reporteControlador = new ReporteControlador();
        this.usuarioActual = null;
    }

    public void iniciar() {
        boolean salir = false;

        while (!salir) {
            if (usuarioActual == null) {
                salir = menuInvitado();
            } else {
                switch (usuarioActual.getRol()) {
                    case DOCENTE -> menuDocente();
                    case AUXILIAR_LOGISTICO -> menuAuxiliarLogistico();
                    case ADMINISTRADOR -> menuAdministrador();
                    default -> {
                        System.out.println("Rol no reconocido. Se cerrará la sesión.");
                        cerrarSesion();
                    }
                }
            }
        }

        System.out.println("Programa finalizado.");
    }

    // ==========================
    // MENÚ INICIAL / INVITADO
    // ==========================
    private boolean menuInvitado() {
        System.out.println("==============================================");
        System.out.println("         GESTIONA TU SALON - INICIO");
        System.out.println("==============================================");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        System.out.println("0. Salir");
        System.out.println("==============================================");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> iniciarSesion();
            case 2 -> registrarUsuario();
            case 0 -> {
                return true;
            }
            default -> System.out.println("Opción inválida.");
        }

        System.out.println();
        return false;
    }

    // ==========================
    // MENÚ DOCENTE
    // ==========================
    private void menuDocente() {
        System.out.println("==============================================");
        System.out.println("      MENÚ DOCENTE - " + usuarioActual.getNombre());
        System.out.println("==============================================");
        System.out.println("1. Listar espacios");
        System.out.println("2. Consultar espacios disponibles");
        System.out.println("3. Crear reserva");
        System.out.println("4. Ver mis reservas");
        System.out.println("5. Cancelar reserva");
        System.out.println("6. Registrar incidente");
        System.out.println("7. Cerrar sesión");
        System.out.println("==============================================");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> listarEspacios();
            case 2 -> consultarEspaciosDisponibles();
            case 3 -> crearReserva();
            case 4 -> listarReservasPropias();
            case 5 -> cancelarReserva();
            case 6 -> registrarIncidente();
            case 7 -> cerrarSesion();
            default -> System.out.println("Opción inválida.");
        }

        System.out.println();
    }

    // ==========================
    // MENÚ AUXILIAR LOGÍSTICO
    // ==========================
    private void menuAuxiliarLogistico() {
        System.out.println("==============================================");
        System.out.println(" MENÚ AUXILIAR LOGÍSTICO - " + usuarioActual.getNombre());
        System.out.println("==============================================");
        System.out.println("1. Listar incidentes");
        System.out.println("2. Cerrar incidente");
        System.out.println("3. Registrar requerimiento logístico");
        System.out.println("4. Crear orden de servicio");
        System.out.println("5. Listar órdenes de servicio");
        System.out.println("6. Actualizar estado de orden");
        System.out.println("7. Listar espacios");
        System.out.println("8. Cerrar sesión");
        System.out.println("==============================================");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> listarIncidentes();
            case 2 -> cerrarIncidente();
            case 3 -> registrarRequerimientoLogistico();
            case 4 -> crearOrdenServicio();
            case 5 -> listarOrdenesServicio();
            case 6 -> actualizarEstadoOrden();
            case 7 -> listarEspacios();
            case 8 -> cerrarSesion();
            default -> System.out.println("Opción inválida.");
        }

        System.out.println();
    }

    // ==========================
    // MENÚ ADMINISTRADOR
    // ==========================
    private void menuAdministrador() {
        System.out.println("==============================================");
        System.out.println("   MENÚ ADMINISTRADOR - " + usuarioActual.getNombre());
        System.out.println("==============================================");
        System.out.println("1. Listar usuarios");
        System.out.println("2. Registrar usuario");
        System.out.println("3. Listar espacios");
        System.out.println("4. Consultar espacios disponibles");
        System.out.println("5. Listar todas las reservas");
        System.out.println("6. Aprobar reserva");
        System.out.println("7. Registrar incidente");
        System.out.println("8. Listar incidentes");
        System.out.println("9. Generar reporte básico");
        System.out.println("10. Calcular KPI de ocupación");
        System.out.println("11. Mostrar espacios más solicitados");
        System.out.println("12. Cerrar sesión");
        System.out.println("==============================================");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> listarUsuarios();
            case 2 -> registrarUsuario();
            case 3 -> listarEspacios();
            case 4 -> consultarEspaciosDisponibles();
            case 5 -> listarReservas();
            case 6 -> aprobarReserva();
            case 7 -> registrarIncidente();
            case 8 -> listarIncidentes();
            case 9 -> generarReporteBasico();
            case 10 -> calcularKPI();
            case 11 -> mostrarEspaciosMasSolicitados();
            case 12 -> cerrarSesion();
            default -> System.out.println("Opción inválida.");
        }

        System.out.println();
    }

    // ==========================
    // AUTENTICACIÓN Y SESIÓN
    // ==========================
    private void iniciarSesion() {
        System.out.println("---- INICIAR SESIÓN ----");
        String correo = leerCadena("Correo: ");
        String clave = leerCadena("Clave: ");

        Usuario usuario = usuarioControlador.iniciarSesion(correo, clave);

        if (usuario != null) {
            this.usuarioActual = usuario;
            System.out.println("Inicio de sesión exitoso.");
            System.out.println("Bienvenido: " + usuarioActual.getNombre());
            System.out.println("Rol: " + usuarioActual.getRol());
        } else {
            System.out.println("No fue posible iniciar sesión.");
        }
    }

    private void cerrarSesion() {
        System.out.println("Sesión cerrada.");
        this.usuarioActual = null;
    }

    // ==========================
    // USUARIOS
    // ==========================
    private void listarUsuarios() {
        System.out.println("---- LISTA DE USUARIOS ----");
        List<Usuario> usuarios = usuarioControlador.obtenerUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    private void registrarUsuario() {
        System.out.println("---- REGISTRAR USUARIO ----");

        String nombre = leerCadena("Nombre: ");
        String apellido = leerCadena("Apellido: ");
        String correo = leerCadena("Correo: ");
        String clave = leerCadena("Clave: ");
        String telefono = leerCadena("Teléfono: ");
        int idRol = leerEntero("Id del rol (ejemplo: 1, 2, 3): ");

        String textoPrograma = leerCadena("Id del programa (si no tiene, deje vacío y presione Enter): ");
        Integer idPrograma = null;

        if (!textoPrograma.isBlank()) {
            try {
                idPrograma = Integer.parseInt(textoPrograma);
            } catch (NumberFormatException e) {
                System.out.println("Id de programa inválido. Se guardará como nulo.");
            }
        }

        boolean registrado = usuarioControlador.registrarUsuario(
                nombre, apellido, correo, clave, telefono, idRol, idPrograma
        );

        if (registrado) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("No fue posible registrar el usuario.");
        }
    }

    // ==========================
    // ESPACIOS
    // ==========================
    private void listarEspacios() {
        System.out.println("---- LISTA DE ESPACIOS ----");
        List<EspacioFisico> espacios = espacioControlador.listarEspacios();

        if (espacios.isEmpty()) {
            System.out.println("No hay espacios registrados.");
            return;
        }

        for (EspacioFisico espacio : espacios) {
            System.out.println(espacio);
        }
    }

    private void consultarEspaciosDisponibles() {
        System.out.println("---- CONSULTAR ESPACIOS DISPONIBLES ----");
        LocalDate fecha = leerFecha("Ingrese la fecha (AAAA-MM-DD): ");
        List<EspacioFisico> espacios = espacioControlador.listarEspaciosDisponibles(fecha);

        if (espacios.isEmpty()) {
            System.out.println("No hay espacios disponibles para la fecha indicada.");
            return;
        }

        for (EspacioFisico espacio : espacios) {
            System.out.println(espacio);
        }
    }

    // ==========================
    // RESERVAS
    // ==========================
    private void crearReserva() {
        System.out.println("---- CREAR RESERVA ----");

        int idDocente;
        if (usuarioActual != null && usuarioActual.getRol() == EnumRol.DOCENTE) {
            idDocente = usuarioActual.getIdUsuario();
            System.out.println("Docente autenticado: " + usuarioActual.getNombre() + " (ID: " + idDocente + ")");
        } else {
            idDocente = leerEntero("Id del docente/usuario: ");
        }

        String idEspacio = leerCadena("Id del espacio: ");
        LocalDate fecha = leerFecha("Fecha de la reserva (AAAA-MM-DD): ");
        LocalTime horaInicio = leerHora("Hora de inicio (HH:MM): ");
        LocalTime horaFin = leerHora("Hora de fin (HH:MM): ");

        String textoBloques = leerCadena("Bloques horarios (separados por coma, ejemplo 1,2,3): ");
        List<Integer> bloques = convertirTextoABloques(textoBloques);

        String observacion = leerCadena("Observación: ");

        boolean creada = reservaControlador.crearReserva(
                idDocente, idEspacio, fecha, horaInicio, horaFin, bloques, observacion
        );

        if (creada) {
            System.out.println("Reserva creada correctamente.");
        } else {
            System.out.println("No fue posible crear la reserva.");
        }
    }

    private void listarReservasPropias() {
        if (usuarioActual == null) {
            System.out.println("No hay sesión activa.");
            return;
        }

        System.out.println("---- MIS RESERVAS ----");
        List<Reserva> reservas = reservaControlador.listarReservasPorUsuario(usuarioActual.getIdUsuario());

        if (reservas.isEmpty()) {
            System.out.println("No se encontraron reservas para este usuario.");
            return;
        }

        for (Reserva reserva : reservas) {
            System.out.println(reserva);
        }
    }

    private void listarReservas() {
        System.out.println("---- LISTA DE TODAS LAS RESERVAS ----");
        List<Reserva> reservas = reservaControlador.listarReservas();

        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        for (Reserva reserva : reservas) {
            System.out.println(reserva);
        }
    }

    private void cancelarReserva() {
        System.out.println("---- CANCELAR RESERVA ----");

        int idReserva = leerEntero("Id de la reserva: ");
        String motivo = leerCadena("Motivo de cancelación: ");

        boolean cancelada = reservaControlador.cancelarReserva(idReserva, motivo);

        if (cancelada) {
            System.out.println("Reserva cancelada correctamente.");
        } else {
            System.out.println("No fue posible cancelar la reserva.");
        }
    }

    private void aprobarReserva() {
        System.out.println("---- APROBAR RESERVA ----");

        int idReserva = leerEntero("Id de la reserva: ");
        boolean aprobada = reservaControlador.aprobarReserva(idReserva);

        if (aprobada) {
            System.out.println("Reserva aprobada correctamente.");
        } else {
            System.out.println("No fue posible aprobar la reserva.");
        }
    }

    // ==========================
    // INCIDENTES
    // ==========================
    private void registrarIncidente() {
        System.out.println("---- REGISTRAR INCIDENTE ----");

        int idUsuario;
        if (usuarioActual != null) {
            idUsuario = usuarioActual.getIdUsuario();
            System.out.println("Usuario autenticado: " + usuarioActual.getNombre() + " (ID: " + idUsuario + ")");
        } else {
            idUsuario = leerEntero("Id del usuario que reporta: ");
        }

        String descripcion = leerCadena("Descripción del incidente: ");
        int severidad = leerEntero("Severidad (1 a 10): ");

        String textoEspacio = leerCadena("Id del espacio (opcional, Enter si no aplica): ");
        Integer idEspacio = textoEspacio.isBlank() ? null : Integer.parseInt(textoEspacio);

        String textoEquipo = leerCadena("Id del equipo (opcional, Enter si no aplica): ");
        Integer idEquipo = textoEquipo.isBlank() ? null : Integer.parseInt(textoEquipo);

        boolean registrado = incidenteControlador.registrarIncidente(
                idUsuario, descripcion, severidad, idEspacio, idEquipo
        );

        if (registrado) {
            System.out.println("Incidente registrado correctamente.");
        } else {
            System.out.println("No fue posible registrar el incidente.");
        }
    }

    private void listarIncidentes() {
        System.out.println("---- LISTA DE INCIDENTES ----");
        List<Incidente> incidentes = incidenteControlador.listarIncidentes();

        if (incidentes.isEmpty()) {
            System.out.println("No hay incidentes registrados.");
            return;
        }

        for (Incidente incidente : incidentes) {
            System.out.println(incidente);
            System.out.println("Prioridad calculada: " + incidente.asignarPrioridad());
            System.out.println("--------------------------------------");
        }
    }

    private void cerrarIncidente() {
        System.out.println("---- CERRAR INCIDENTE ----");

        int idIncidente = leerEntero("Id del incidente: ");
        boolean cerrado = incidenteControlador.cerrarIncidente(idIncidente);

        if (cerrado) {
            System.out.println("Incidente cerrado correctamente.");
        } else {
            System.out.println("No fue posible cerrar el incidente.");
        }
    }

    // ==========================
    // LOGÍSTICA
    // ==========================
    private void registrarRequerimientoLogistico() {
        System.out.println("---- REGISTRAR REQUERIMIENTO LOGÍSTICO ----");

        int idReserva = leerEntero("Id de la reserva: ");
        int sillasAdicionales = leerEntero("Cantidad de sillas adicionales: ");
        float configuracionAC = leerFlotante("Configuración AC (ejemplo 22.5): ");
        boolean requiereApoyo = leerBooleano("¿Requiere apoyo técnico? (si/no): ");

        String textoCategoria = leerCadena("Id categoría (opcional, Enter si no aplica): ");
        Integer idCategoria = textoCategoria.isBlank() ? null : Integer.parseInt(textoCategoria);

        boolean registrado = logisticaControlador.registrarRequerimiento(
                idReserva, sillasAdicionales, configuracionAC, requiereApoyo, idCategoria
        );

        if (registrado) {
            System.out.println("Requerimiento logístico registrado correctamente.");
        } else {
            System.out.println("No fue posible registrar el requerimiento logístico.");
        }
    }

    private void crearOrdenServicio() {
        System.out.println("---- CREAR ORDEN DE SERVICIO ----");

        int idReserva = leerEntero("Id de la reserva: ");
        int idAuxiliar = leerEntero("Id del auxiliar logístico: ");
        String estadoOrden = leerCadena("Estado inicial de la orden: ");
        String observacion = leerCadena("Observación: ");

        boolean creada = logisticaControlador.crearOrdenServicio(
                idReserva, idAuxiliar, estadoOrden, observacion
        );

        if (creada) {
            System.out.println("Orden de servicio creada correctamente.");
        } else {
            System.out.println("No fue posible crear la orden de servicio.");
        }
    }

    private void listarOrdenesServicio() {
        System.out.println("---- LISTA DE ÓRDENES DE SERVICIO ----");
        List<OrdenServicio> ordenes = logisticaControlador.listarOrdenesServicio();

        if (ordenes.isEmpty()) {
            System.out.println("No hay órdenes de servicio registradas.");
            return;
        }

        for (OrdenServicio orden : ordenes) {
            System.out.println(orden);
        }
    }

    private void actualizarEstadoOrden() {
        System.out.println("---- ACTUALIZAR ESTADO DE ORDEN ----");

        int idOrden = leerEntero("Id de la orden: ");
        String nuevoEstado = leerCadena("Nuevo estado: ");

        boolean actualizado = logisticaControlador.actualizarEstadoOrden(idOrden, nuevoEstado);

        if (actualizado) {
            System.out.println("Estado de la orden actualizado correctamente.");
        } else {
            System.out.println("No fue posible actualizar la orden.");
        }
    }

    // ==========================
    // REPORTES
    // ==========================
    private void generarReporteBasico() {
        System.out.println("---- GENERAR REPORTE BÁSICO ----");

        String tipo = leerCadena("Tipo de reporte: ");
        ReporteGerencial reporte = reporteControlador.generarReporteBasico(tipo);

        System.out.println("Reporte generado:");
        System.out.println(reporte);
        System.out.println(reporteControlador.exportarReportePDF());
    }

    private void calcularKPI() {
        System.out.println("---- KPI DE OCUPACIÓN ----");

        double kpi = reporteControlador.calcularKPIOcupacion();
        System.out.println("KPI de ocupación aproximado: " + kpi + "%");
    }

    private void mostrarEspaciosMasSolicitados() {
        System.out.println("---- ESPACIOS MÁS SOLICITADOS ----");

        List<String> espacios = reporteControlador.obtenerEspaciosMasSolicitados();

        if (espacios.isEmpty()) {
            System.out.println("No hay información disponible.");
            return;
        }

        for (String espacio : espacios) {
            System.out.println("Espacio: " + espacio);
        }
    }

    // ==========================
    // MÉTODOS AUXILIARES
    // ==========================
    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Debe ingresar un número entero.");
            }
        }
    }

    private float leerFlotante(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Float.parseFloat(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Debe ingresar un número decimal.");
            }
        }
    }

    private String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Fecha inválida. Use el formato AAAA-MM-DD.");
            }
        }
    }

    private LocalTime leerHora(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Hora inválida. Use el formato HH:MM.");
            }
        }
    }

    private boolean leerBooleano(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim().toLowerCase();

            if (valor.equals("si") || valor.equals("sí") || valor.equals("s")) {
                return true;
            }

            if (valor.equals("no") || valor.equals("n")) {
                return false;
            }

            System.out.println("Respuesta inválida. Escriba si o no.");
        }
    }

    private List<Integer> convertirTextoABloques(String texto) {
        List<Integer> bloques = new ArrayList<>();

        if (texto == null || texto.isBlank()) {
            return bloques;
        }

        String[] partes = texto.split(",");

        for (String parte : partes) {
            try {
                bloques.add(Integer.parseInt(parte.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Bloque inválido ignorado: " + parte);
            }
        }

        return bloques;
    }
}