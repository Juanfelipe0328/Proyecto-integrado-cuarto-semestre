
DROP DATABASE IF EXISTS gestiona_tu_salon;
CREATE DATABASE gestiona_tu_salon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestiona_tu_salon;


-- 1. SEGURIDAD Y ESTRUCTURA INSTITUCIONAL


CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE Facultad (
    id_facultad INT AUTO_INCREMENT PRIMARY KEY,
    nombre_facultad VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE Programa (
    id_programa INT AUTO_INCREMENT PRIMARY KEY,
    nombre_programa VARCHAR(100) NOT NULL,
    id_facultad INT NOT NULL,
    CONSTRAINT fk_programa_facultad
        FOREIGN KEY (id_facultad) REFERENCES Facultad(id_facultad)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol INT NOT NULL,
    id_programa INT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_programa
        FOREIGN KEY (id_programa) REFERENCES Programa(id_programa)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);


-- 2. INFRAESTRUCTURA INSTITUCIONAL


CREATE TABLE Sede (
    id_sede INT AUTO_INCREMENT PRIMARY KEY,
    nombre_sede VARCHAR(100) NOT NULL UNIQUE,
    direccion VARCHAR(150) NOT NULL
);

CREATE TABLE Tipo_Espacio (
    id_tipo_espacio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE Espacio (
    id_espacio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_espacio VARCHAR(80) NOT NULL,
    bloque VARCHAR(30),
    piso VARCHAR(10),
    capacidad INT NOT NULL,
    estado ENUM('Disponible', 'Mantenimiento', 'Inactivo') NOT NULL DEFAULT 'Disponible',
    id_sede INT NOT NULL,
    id_tipo_espacio INT NOT NULL,
    CONSTRAINT uq_espacio UNIQUE (nombre_espacio, id_sede),
    CONSTRAINT fk_espacio_sede
        FOREIGN KEY (id_sede) REFERENCES Sede(id_sede)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_espacio_tipo
        FOREIGN KEY (id_tipo_espacio) REFERENCES Tipo_Espacio(id_tipo_espacio)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);


-- 3. BLOQUES HORARIOS


CREATE TABLE Bloque_Horario (
    id_bloque INT AUTO_INCREMENT PRIMARY KEY,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    descripcion VARCHAR(50),
    orden_bloque INT NOT NULL UNIQUE,
    CONSTRAINT chk_bloque_valido CHECK (hora_inicio < hora_fin)
);


-- 4. CALENDARIO DE RESTRICCIONES


CREATE TABLE Restriccion_Calendario (
    id_restriccion INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    motivo VARCHAR(150) NOT NULL,
    aplica_toda_institucion BOOLEAN NOT NULL DEFAULT TRUE,
    id_espacio INT NULL,
    CONSTRAINT chk_fechas_restriccion CHECK (fecha_inicio <= fecha_fin),
    CONSTRAINT fk_restriccion_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);


-- 5. RESERVAS


CREATE TABLE Reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva DATE NOT NULL,
    estado_reserva ENUM('Pendiente', 'Aprobada', 'Cancelada', 'Finalizada') NOT NULL DEFAULT 'Pendiente',
    motivo_cancelacion VARCHAR(255),
    observacion VARCHAR(255),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    id_espacio INT NOT NULL,
    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_reserva_espacio_fecha_estado
ON Reserva (id_espacio, fecha_reserva, estado_reserva);

-- Relación N:M entre Reserva y Bloque_Horario
CREATE TABLE Reserva_Bloque (
    id_reserva_bloque INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_bloque INT NOT NULL,
    CONSTRAINT uq_reserva_bloque UNIQUE (id_reserva, id_bloque),
    CONSTRAINT fk_reserva_bloque_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_reserva_bloque_bloque
        FOREIGN KEY (id_bloque) REFERENCES Bloque_Horario(id_bloque)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_reserva_bloque_bloque
ON Reserva_Bloque (id_bloque);

-- 6. INVENTARIO TECNOLOGICO


CREATE TABLE Categoria_Equipo (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(60) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

CREATE TABLE Equipo (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    serial VARCHAR(100) NOT NULL UNIQUE,
    modelo VARCHAR(100),
    marca VARCHAR(100),
    estado_equipo ENUM('Disponible', 'Prestado', 'En mantenimiento', 'Dado de baja') NOT NULL DEFAULT 'Disponible',
    tipo_asignacion ENUM('Fijo', 'Movil') NOT NULL DEFAULT 'Movil',
    id_categoria INT NOT NULL,
    id_espacio INT NULL,
    CONSTRAINT fk_equipo_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_equipo_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- Inventario fijo resumido por espacio/categoria
CREATE TABLE Espacio_Equipamiento (
    id_espacio INT NOT NULL,
    id_categoria INT NOT NULL,
    cantidad_fija INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_espacio, id_categoria),
    CONSTRAINT fk_espacio_equipamiento_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_espacio_equipamiento_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 7. REQUERIMIENTOS LOGISTICOS DE LA RESERVA

CREATE TABLE Detalle_Requerimiento (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_categoria INT NULL,
    cantidad_solicitada INT NOT NULL DEFAULT 1,
    configuracion_especial TEXT,
    estado_preparacion ENUM('Pendiente', 'En proceso', 'Listo') NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT fk_detalle_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_detalle_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- Equipos móviles efectivamente asignados a una reserva
CREATE TABLE Reserva_Equipo (
    id_reserva_equipo INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_equipo INT NOT NULL,
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reserva_equipo UNIQUE (id_reserva, id_equipo),
    CONSTRAINT fk_reserva_equipo_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_reserva_equipo_equipo
        FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 8. ORDENES DE SERVICIO PARA AUXILIAR DE LOGISTICA
CREATE TABLE Orden_Servicio (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_auxiliar INT NOT NULL,
    estado_orden ENUM('Pendiente', 'En proceso', 'Completada', 'Cancelada') NOT NULL DEFAULT 'Pendiente',
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre DATETIME NULL,
    observacion TEXT,
    CONSTRAINT uq_orden_reserva UNIQUE (id_reserva),
    CONSTRAINT fk_orden_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_orden_auxiliar
        FOREIGN KEY (id_auxiliar) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 9. INCIDENTES, ENCUESTAS Y NOTIFICACIONES

CREATE TABLE Incidente (
    id_incidente INT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT NOT NULL,
    severidad ENUM('Baja', 'Media', 'Alta', 'Critica') NOT NULL DEFAULT 'Media',
    fecha_reporte DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_incidente ENUM('Reportado', 'En reparacion', 'Solucionado') NOT NULL DEFAULT 'Reportado',
    id_usuario INT NOT NULL,
    id_espacio INT NULL,
    id_equipo INT NULL,
    CONSTRAINT fk_incidente_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_incidente_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    CONSTRAINT fk_incidente_equipo
        FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE Encuesta (
    id_encuesta INT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha DATE NOT NULL,
    id_usuario INT NOT NULL,
    id_reserva INT NOT NULL,
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT fk_encuesta_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_encuesta_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE Notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    mensaje VARCHAR(200) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_notificacion_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 10. DATOS BASE

INSERT INTO Rol (nombre_rol, descripcion) VALUES
('Administrador', 'Gestion tecnica y de usuarios'),
('Coordinador', 'Planeacion macro y aprobacion de espacios'),
('Auxiliar Logistica', 'Preparacion fisica y tecnica de espacios'),
('Directivo', 'Consulta de indicadores y reportes institucionales'),
('Docente', 'Solicitud de reservas y requerimientos');

INSERT INTO Bloque_Horario (hora_inicio, hora_fin, descripcion, orden_bloque) VALUES
('07:00:00', '08:00:00', 'Bloque 1', 1),
('08:00:00', '09:00:00', 'Bloque 2', 2),
('09:00:00', '10:00:00', 'Bloque 3', 3),
('10:00:00', '11:00:00', 'Bloque 4', 4),
('11:00:00', '12:00:00', 'Bloque 5', 5),
('12:00:00', '13:00:00', 'Bloque 6', 6),
('13:00:00', '14:00:00', 'Bloque 7', 7),
('14:00:00', '15:00:00', 'Bloque 8', 8),
('15:00:00', '16:00:00', 'Bloque 9', 9),
('16:00:00', '17:00:00', 'Bloque 10', 10),
('17:00:00', '18:00:00', 'Bloque 11', 11),
('18:00:00', '19:00:00', 'Bloque 12', 12),
('19:00:00', '20:00:00', 'Bloque 13', 13),
('20:00:00', '21:00:00', 'Bloque 14', 14),
('21:00:00', '22:00:00', 'Bloque 15', 15);

INSERT INTO Categoria_Equipo (nombre_categoria, descripcion) VALUES
('Videobeam', 'Proyector de video'),
('Laptop', 'Computador portatil'),
('Microfono', 'Microfono inalambrico o cableado'),
('Sonido', 'Parlantes o sistema de sonido'),
('Pantalla', 'Pantalla o monitor adicional');

-- 11. TRIGGERS DE VALIDACION

DELIMITER $$

-- Evita cruces de reservas por espacio + fecha + bloque
CREATE TRIGGER trg_validar_reserva_bloque_insert
BEFORE INSERT ON Reserva_Bloque
FOR EACH ROW
BEGIN
    DECLARE v_id_espacio INT;
    DECLARE v_fecha DATE;
    DECLARE v_conflictos INT DEFAULT 0;

    SELECT id_espacio, fecha_reserva
      INTO v_id_espacio, v_fecha
    FROM Reserva
    WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*)
      INTO v_conflictos
    FROM Reserva_Bloque rb
    INNER JOIN Reserva r
        ON r.id_reserva = rb.id_reserva
    WHERE r.id_espacio = v_id_espacio
      AND r.fecha_reserva = v_fecha
      AND rb.id_bloque = NEW.id_bloque
      AND r.estado_reserva IN ('Pendiente', 'Aprobada');

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: ese bloque ya esta reservado para el espacio y la fecha seleccionados';
    END IF;
END$$

CREATE TRIGGER trg_validar_reserva_bloque_update
BEFORE UPDATE ON Reserva_Bloque
FOR EACH ROW
BEGIN
    DECLARE v_id_espacio INT;
    DECLARE v_fecha DATE;
    DECLARE v_conflictos INT DEFAULT 0;

    SELECT id_espacio, fecha_reserva
      INTO v_id_espacio, v_fecha
    FROM Reserva
    WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*)
      INTO v_conflictos
    FROM Reserva_Bloque rb
    INNER JOIN Reserva r
        ON r.id_reserva = rb.id_reserva
    WHERE r.id_espacio = v_id_espacio
      AND r.fecha_reserva = v_fecha
      AND rb.id_bloque = NEW.id_bloque
      AND r.estado_reserva IN ('Pendiente', 'Aprobada')
      AND rb.id_reserva_bloque <> NEW.id_reserva_bloque;

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: ese bloque ya esta reservado para el espacio y la fecha seleccionados';
    END IF;
END$$

-- Evita mover una reserva a un espacio/fecha que ya tenga conflicto
CREATE TRIGGER trg_validar_cambio_reserva
BEFORE UPDATE ON Reserva
FOR EACH ROW
BEGIN
    DECLARE v_conflictos INT DEFAULT 0;

    IF (OLD.id_espacio <> NEW.id_espacio) OR (OLD.fecha_reserva <> NEW.fecha_reserva) THEN

        SELECT COUNT(*)
          INTO v_conflictos
        FROM Reserva_Bloque rb_actual
        INNER JOIN Reserva_Bloque rb_otra
            ON rb_otra.id_bloque = rb_actual.id_bloque
        INNER JOIN Reserva r_otra
            ON r_otra.id_reserva = rb_otra.id_reserva
        WHERE rb_actual.id_reserva = OLD.id_reserva
          AND r_otra.id_reserva <> OLD.id_reserva
          AND r_otra.id_espacio = NEW.id_espacio
          AND r_otra.fecha_reserva = NEW.fecha_reserva
          AND r_otra.estado_reserva IN ('Pendiente', 'Aprobada');

        IF v_conflictos > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Conflicto: el cambio genera cruce con otra reserva';
        END IF;

    END IF;
END$$

-- Evita asignar un equipo movil a dos reservas activas al mismo tiempo
CREATE TRIGGER trg_validar_equipo_reserva_insert
BEFORE INSERT ON Reserva_Equipo
FOR EACH ROW
BEGIN
    DECLARE v_fecha DATE;
    DECLARE v_conflictos INT DEFAULT 0;

    SELECT fecha_reserva
      INTO v_fecha
    FROM Reserva
    WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*)
      INTO v_conflictos
    FROM Reserva_Equipo re
    INNER JOIN Reserva r
        ON r.id_reserva = re.id_reserva
    WHERE re.id_equipo = NEW.id_equipo
      AND r.fecha_reserva = v_fecha
      AND r.estado_reserva IN ('Pendiente', 'Aprobada');

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: el equipo ya esta asignado en otra reserva activa para esa fecha';
    END IF;
END$$

DELIMITER ;