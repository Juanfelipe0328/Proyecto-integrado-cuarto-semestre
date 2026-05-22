
--  GESTIONA TU SALÓN

DROP DATABASE IF EXISTS gestiona_tu_salon;
CREATE DATABASE gestiona_tu_salon CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestiona_tu_salon;


-- SECCIÓN 1: SEGURIDAD Y ESTRUCTURA INSTITUCIONAL

CREATE TABLE Rol (
    id_rol       INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol   VARCHAR(50)  NOT NULL UNIQUE,
    descripcion  VARCHAR(150)
);

CREATE TABLE Facultad (
    id_facultad      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_facultad  VARCHAR(100) NOT NULL UNIQUE,
    descripcion      VARCHAR(150)
);

CREATE TABLE Programa (
    id_programa      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_programa  VARCHAR(100) NOT NULL,
    id_facultad      INT NOT NULL,
    CONSTRAINT fk_programa_facultad
        FOREIGN KEY (id_facultad) REFERENCES Facultad(id_facultad)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Usuario (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    correo          VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    telefono        VARCHAR(20),
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol          INT NOT NULL,
    id_programa     INT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_usuario_programa
        FOREIGN KEY (id_programa) REFERENCES Programa(id_programa)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- SECCIÓN 2: INFRAESTRUCTURA INSTITUCIONAL

CREATE TABLE Sede (
    id_sede      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_sede  VARCHAR(100) NOT NULL UNIQUE,
    direccion    VARCHAR(150) NOT NULL
);

CREATE TABLE Tipo_Espacio (
    id_tipo_espacio  INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion      VARCHAR(150)
);

CREATE TABLE Espacio (
    id_espacio      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_espacio  VARCHAR(80)  NOT NULL,
    bloque          VARCHAR(30),
    piso            VARCHAR(10),
    capacidad       INT NOT NULL,
    estado          ENUM('Disponible','Mantenimiento','Inactivo') NOT NULL DEFAULT 'Disponible',
    id_sede         INT NOT NULL,
    id_tipo_espacio INT NOT NULL,
    CONSTRAINT uq_espacio
        UNIQUE (nombre_espacio, id_sede),
    CONSTRAINT fk_espacio_sede
        FOREIGN KEY (id_sede) REFERENCES Sede(id_sede)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_espacio_tipo
        FOREIGN KEY (id_tipo_espacio) REFERENCES Tipo_Espacio(id_tipo_espacio)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- SECCIÓN 3: BLOQUES HORARIOS

CREATE TABLE Bloque_Horario (
    id_bloque     INT AUTO_INCREMENT PRIMARY KEY,
    hora_inicio   TIME NOT NULL,
    hora_fin      TIME NOT NULL,
    descripcion   VARCHAR(50),
    orden_bloque  INT NOT NULL UNIQUE,
    CONSTRAINT chk_bloque_valido CHECK (hora_inicio < hora_fin)
);

-- SECCIÓN 4: CALENDARIO DE RESTRICCIONES

CREATE TABLE Restriccion_Calendario (
    id_restriccion         INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio           DATE NOT NULL,
    fecha_fin              DATE NOT NULL,
    motivo                 VARCHAR(150) NOT NULL,
    aplica_toda_institucion BOOLEAN NOT NULL DEFAULT TRUE,
    id_espacio             INT NULL,
    CONSTRAINT chk_fechas_restriccion CHECK (fecha_inicio <= fecha_fin),
    CONSTRAINT fk_restriccion_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- SECCIÓN 5: RESERVAS

CREATE TABLE Reserva (
    id_reserva        INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva     DATE NOT NULL,
    estado_reserva    ENUM('Pendiente','Aprobada','Cancelada','Finalizada') NOT NULL DEFAULT 'Pendiente',
    motivo_cancelacion VARCHAR(255),
    observacion       VARCHAR(255),
    fecha_creacion    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario        INT NOT NULL,
    id_espacio        INT NOT NULL,
    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reserva_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_reserva_espacio_fecha_estado
    ON Reserva (id_espacio, fecha_reserva, estado_reserva);

CREATE TABLE Reserva_Bloque (
    id_reserva_bloque INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva        INT NOT NULL,
    id_bloque         INT NOT NULL,
    CONSTRAINT uq_reserva_bloque UNIQUE (id_reserva, id_bloque),
    CONSTRAINT fk_reserva_bloque_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_reserva_bloque_bloque
        FOREIGN KEY (id_bloque) REFERENCES Bloque_Horario(id_bloque)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_reserva_bloque_bloque
    ON Reserva_Bloque (id_bloque);

-- SECCIÓN 6: INVENTARIO TECNOLÓGICO

CREATE TABLE Categoria_Equipo (
    id_categoria     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(60)  NOT NULL UNIQUE,
    descripcion      VARCHAR(150)
);

CREATE TABLE Equipo (
    id_equipo       INT AUTO_INCREMENT PRIMARY KEY,
    serial          VARCHAR(100) NOT NULL UNIQUE,
    modelo          VARCHAR(100),
    marca           VARCHAR(100),
    estado_equipo   ENUM('Disponible','Prestado','En mantenimiento','Dado de baja') NOT NULL DEFAULT 'Disponible',
    tipo_asignacion ENUM('Fijo','Movil') NOT NULL DEFAULT 'Movil',
    id_categoria    INT NOT NULL,
    id_espacio      INT NULL,
    CONSTRAINT fk_equipo_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_equipo_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE Espacio_Equipamiento (
    id_espacio    INT NOT NULL,
    id_categoria  INT NOT NULL,
    cantidad_fija INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id_espacio, id_categoria),
    CONSTRAINT fk_espacio_equipamiento_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_espacio_equipamiento_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- SECCIÓN 7: REQUERIMIENTOS LOGÍSTICOS

CREATE TABLE Detalle_Requerimiento (
    id_detalle           INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva           INT NOT NULL,
    id_categoria         INT NULL,
    cantidad_solicitada  INT NOT NULL DEFAULT 1,
    configuracion_especial TEXT,
    estado_preparacion   ENUM('Pendiente','En proceso','Listo') NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT fk_detalle_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_categoria
        FOREIGN KEY (id_categoria) REFERENCES Categoria_Equipo(id_categoria)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE Reserva_Equipo (
    id_reserva_equipo INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva        INT NOT NULL,
    id_equipo         INT NOT NULL,
    fecha_asignacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_reserva_equipo UNIQUE (id_reserva, id_equipo),
    CONSTRAINT fk_reserva_equipo_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_reserva_equipo_equipo
        FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- SECCIÓN 8: ÓRDENES DE SERVICIO

CREATE TABLE Orden_Servicio (
    id_orden         INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva       INT NOT NULL,
    id_auxiliar      INT NOT NULL,
    estado_orden     ENUM('Pendiente','En proceso','Completada','Cancelada') NOT NULL DEFAULT 'Pendiente',
    fecha_asignacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre     DATETIME NULL,
    observacion      TEXT,
    CONSTRAINT uq_orden_reserva UNIQUE (id_reserva),
    CONSTRAINT fk_orden_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_orden_auxiliar
        FOREIGN KEY (id_auxiliar) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- SECCIÓN 9: INCIDENTES, ENCUESTAS Y NOTIFICACIONES-- ============================================================

CREATE TABLE Incidente (
    id_incidente     INT AUTO_INCREMENT PRIMARY KEY,
    descripcion      TEXT NOT NULL,
    severidad        ENUM('Baja','Media','Alta','Critica') NOT NULL DEFAULT 'Media',
    fecha_reporte    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_incidente ENUM('Reportado','En reparacion','Solucionado') NOT NULL DEFAULT 'Reportado',
    id_usuario       INT NOT NULL,
    id_espacio       INT NULL,
    id_equipo        INT NULL,
    CONSTRAINT fk_incidente_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_incidente_espacio
        FOREIGN KEY (id_espacio) REFERENCES Espacio(id_espacio)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_incidente_equipo
        FOREIGN KEY (id_equipo) REFERENCES Equipo(id_equipo)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE Encuesta (
    id_encuesta  INT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario   TEXT,
    fecha        DATE NOT NULL,
    id_usuario   INT NOT NULL,
    id_reserva   INT NOT NULL,
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT fk_encuesta_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_encuesta_reserva
        FOREIGN KEY (id_reserva) REFERENCES Reserva(id_reserva)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Notificacion (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    mensaje         VARCHAR(200) NOT NULL,
    fecha           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido           BOOLEAN NOT NULL DEFAULT FALSE,
    id_usuario      INT NOT NULL,
    CONSTRAINT fk_notificacion_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- SECCIÓN 10: TRIGGERS DE VALIDACIÓN

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
    FROM Reserva WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*) INTO v_conflictos
    FROM Reserva_Bloque rb
    INNER JOIN Reserva r ON r.id_reserva = rb.id_reserva
    WHERE r.id_espacio = v_id_espacio
      AND r.fecha_reserva = v_fecha
      AND rb.id_bloque = NEW.id_bloque
      AND r.estado_reserva IN ('Pendiente','Aprobada');

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: ese bloque ya esta reservado para el espacio y fecha seleccionados';
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
    FROM Reserva WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*) INTO v_conflictos
    FROM Reserva_Bloque rb
    INNER JOIN Reserva r ON r.id_reserva = rb.id_reserva
    WHERE r.id_espacio = v_id_espacio
      AND r.fecha_reserva = v_fecha
      AND rb.id_bloque = NEW.id_bloque
      AND r.estado_reserva IN ('Pendiente','Aprobada')
      AND rb.id_reserva_bloque <> NEW.id_reserva_bloque;

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: ese bloque ya esta reservado para el espacio y fecha seleccionados';
    END IF;
END$$

CREATE TRIGGER trg_validar_cambio_reserva
BEFORE UPDATE ON Reserva
FOR EACH ROW
BEGIN
    DECLARE v_conflictos INT DEFAULT 0;

    IF (OLD.id_espacio <> NEW.id_espacio) OR (OLD.fecha_reserva <> NEW.fecha_reserva) THEN
        SELECT COUNT(*) INTO v_conflictos
        FROM Reserva_Bloque rb_actual
        INNER JOIN Reserva_Bloque rb_otra ON rb_otra.id_bloque = rb_actual.id_bloque
        INNER JOIN Reserva r_otra ON r_otra.id_reserva = rb_otra.id_reserva
        WHERE rb_actual.id_reserva = OLD.id_reserva
          AND r_otra.id_reserva <> OLD.id_reserva
          AND r_otra.id_espacio = NEW.id_espacio
          AND r_otra.fecha_reserva = NEW.fecha_reserva
          AND r_otra.estado_reserva IN ('Pendiente','Aprobada');

        IF v_conflictos > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Conflicto: el cambio genera cruce con otra reserva';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_validar_equipo_reserva_insert
BEFORE INSERT ON Reserva_Equipo
FOR EACH ROW
BEGIN
    DECLARE v_fecha DATE;
    DECLARE v_conflictos INT DEFAULT 0;

    SELECT fecha_reserva INTO v_fecha
    FROM Reserva WHERE id_reserva = NEW.id_reserva;

    SELECT COUNT(*) INTO v_conflictos
    FROM Reserva_Equipo re
    INNER JOIN Reserva r ON r.id_reserva = re.id_reserva
    WHERE re.id_equipo = NEW.id_equipo
      AND r.fecha_reserva = v_fecha
      AND r.estado_reserva IN ('Pendiente','Aprobada');

    IF v_conflictos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Conflicto: el equipo ya esta asignado en otra reserva activa para esa fecha';
    END IF;
END$$

DELIMITER ;

-- SECCIÓN 11: DATOS BASE DEL SISTEMA

-- Roles del sistema
INSERT INTO Rol (nombre_rol, descripcion) VALUES
('Administrador',    'Gestion tecnica y de usuarios'),
('Coordinador',      'Planeacion macro y aprobacion de espacios'),
('Auxiliar Logistica','Preparacion fisica y tecnica de espacios'),
('Directivo',        'Consulta de indicadores y reportes institucionales'),
('Docente',          'Solicitud de reservas y requerimientos');

-- Bloques horarios (15 bloques de 1 hora: 7am a 10pm)
INSERT INTO Bloque_Horario (hora_inicio, hora_fin, descripcion, orden_bloque) VALUES
('07:00','08:00','Bloque 1',  1),
('08:00','09:00','Bloque 2',  2),
('09:00','10:00','Bloque 3',  3),
('10:00','11:00','Bloque 4',  4),
('11:00','12:00','Bloque 5',  5),
('12:00','13:00','Bloque 6',  6),
('13:00','14:00','Bloque 7',  7),
('14:00','15:00','Bloque 8',  8),
('15:00','16:00','Bloque 9',  9),
('16:00','17:00','Bloque 10',10),
('17:00','18:00','Bloque 11',11),
('18:00','19:00','Bloque 12',12),
('19:00','20:00','Bloque 13',13),
('20:00','21:00','Bloque 14',14),
('21:00','22:00','Bloque 15',15);

-- Categorías de equipo
INSERT INTO Categoria_Equipo (nombre_categoria, descripcion) VALUES
('Videobeam','Proyector de video'),
('Laptop',   'Computador portatil'),
('Microfono','Microfono inalambrico o cableado'),
('Sonido',   'Parlantes o sistema de sonido'),
('Pantalla', 'Pantalla o monitor adicional');

-- SECCIÓN 12: DATOS DE DEMOSTRACIÓN

-- Facultad y programa
INSERT INTO Facultad (nombre_facultad, descripcion) VALUES
('Facultad de Ingenieria', 'Ingenieria y tecnologia');

INSERT INTO Programa (nombre_programa, id_facultad) VALUES
('Tecnologia en Sistemas', 1);

-- Sede principal
INSERT INTO Sede (nombre_sede, direccion) VALUES
('Sede Principal UNIAJC', 'Calle 65 No. 3G-28, Cali');

-- Tipos de espacio
INSERT INTO Tipo_Espacio (nombre_tipo, descripcion) VALUES
('Salon',             'Salon de clase tradicional'),
('Laboratorio',       'Laboratorio de computo'),
('Auditorio',         'Auditorio para eventos'),
('Sala de Reuniones', 'Sala para reuniones y asesorias');

-- Usuarios (uno por cada rol)
-- Contraseña de todos: 1234
INSERT INTO Usuario (nombre, apellido, correo, contrasena_hash, telefono, activo, id_rol, id_programa) VALUES
('Admin',   'Sistema',   'admin@uniajc.edu.co',       '1234', '3000000001', TRUE, 1, NULL),
('Carlos',  'Ramirez',   'coordinador@uniajc.edu.co', '1234', '3000000002', TRUE, 2, NULL),
('Luis',    'Morales',   'auxiliar@uniajc.edu.co',    '1234', '3000000003', TRUE, 3, NULL),
('Diana',   'Directiva', 'directivo@uniajc.edu.co',   '1234', '3000000004', TRUE, 4, NULL),
('Maria',   'Gonzalez',  'docente1@uniajc.edu.co',    '1234', '3000000005', TRUE, 5, 1),
('Juan',    'Perez',     'docente2@uniajc.edu.co',    '1234', '3000000006', TRUE, 5, 1);

-- Espacios físicos
INSERT INTO Espacio (nombre_espacio, bloque, piso, capacidad, estado, id_sede, id_tipo_espacio) VALUES
('Salon 101',         'A', '1', 40,  'Disponible', 1, 1),
('Salon 202',         'A', '2', 35,  'Disponible', 1, 1),
('Laboratorio 301',   'B', '3', 25,  'Disponible', 1, 2),
('Auditorio Central', 'C', '1', 150, 'Disponible', 1, 3),
('Sala de Sistemas',  'B', '1', 30,  'Disponible', 1, 2),
('Sala de Reuniones', 'A', '1', 20,  'Disponible', 1, 4);

-- Equipamiento fijo por espacio
INSERT INTO Espacio_Equipamiento (id_espacio, id_categoria, cantidad_fija) VALUES
(1, 1, 1),   -- Salon 101:        1 Videobeam
(2, 1, 1),   -- Salon 202:        1 Videobeam
(3, 1, 1),   -- Laboratorio 301:  1 Videobeam
(3, 2, 20),  -- Laboratorio 301:  20 Laptops
(4, 1, 2),   -- Auditorio:        2 Videobeams
(4, 3, 4),   -- Auditorio:        4 Microfonos
(4, 4, 1),   -- Auditorio:        1 Sistema de Sonido
(5, 1, 1),   -- Sala de Sistemas: 1 Videobeam
(5, 2, 30),  -- Sala de Sistemas: 30 Laptops
(6, 1, 1);   -- Sala Reuniones:   1 Videobeam

-- Equipos móviles individuales
INSERT INTO Equipo (serial, modelo, marca, estado_equipo, tipo_asignacion, id_categoria, id_espacio) VALUES
('VB-001', 'EB-X41',       'Epson',  'Disponible', 'Fijo',  1, 1),
('VB-002', 'EB-S41',       'Epson',  'Disponible', 'Fijo',  1, 2),
('VB-003', 'MX525A',       'Canon',  'Disponible', 'Movil', 1, NULL),
('LT-001', 'ThinkPad E14', 'Lenovo', 'Disponible', 'Movil', 2, NULL),
('LT-002', 'Aspire 5',     'Acer',   'Disponible', 'Movil', 2, NULL),
('MIC-001','SM58',         'Shure',  'Disponible', 'Movil', 3, NULL);

-- Reservas de ejemplo en diferentes estados
INSERT INTO Reserva (fecha_reserva, estado_reserva, observacion, id_usuario, id_espacio) VALUES
(DATE_ADD(CURDATE(), INTERVAL 1 DAY),  'Pendiente',  'Clase de Programacion 2',     5, 1),
(DATE_ADD(CURDATE(), INTERVAL 1 DAY),  'Aprobada',   'Laboratorio de Base de Datos', 5, 3),
(DATE_ADD(CURDATE(), INTERVAL 2 DAY),  'Pendiente',  'Reunion de docentes',          6, 6),
(DATE_ADD(CURDATE(), INTERVAL 3 DAY),  'Aprobada',   'Evento de grado',              2, 4),
(DATE_ADD(CURDATE(), INTERVAL -1 DAY), 'Finalizada', 'Clase de semestre pasado',     5, 2);

-- Bloques horarios de cada reserva
INSERT INTO Reserva_Bloque (id_reserva, id_bloque) VALUES
(1, 1), (1, 2),    -- Reserva 1: 7am - 9am
(2, 3), (2, 4),    -- Reserva 2: 9am - 11am
(3, 6),            -- Reserva 3: 12pm - 1pm
(4, 7), (4, 8),    -- Reserva 4: 1pm - 3pm
(5, 1), (5, 2);    -- Reserva 5: 7am - 9am

-- Incidentes de ejemplo
INSERT INTO Incidente (descripcion, severidad, id_usuario, id_espacio, id_equipo) VALUES
('El videobeam del Salon 101 no enciende correctamente', 'Alta',  5, 1, 1),
('Silla rota en Laboratorio 301',                        'Baja',  3, 3, NULL),
('Falla en aire acondicionado del Auditorio Central',    'Media', 3, 4, NULL);

-- Requerimientos logísticos
INSERT INTO Detalle_Requerimiento (id_reserva, id_categoria, cantidad_solicitada, configuracion_especial, estado_preparacion) VALUES
(1, 1, 1, 'AC: 20.0, apoyoTecnico: false', 'Pendiente'),
(2, 2, 5, 'AC: 18.0, apoyoTecnico: true',  'En proceso'),
(4, 3, 2, 'AC: 22.0, apoyoTecnico: true',  'Listo');

-- Órdenes de servicio
INSERT INTO Orden_Servicio (id_reserva, id_auxiliar, estado_orden, observacion) VALUES
(2, 3, 'En proceso', 'Preparar equipos de laboratorio para clase'),
(4, 3, 'Pendiente',  'Preparar auditorio para evento de grado');

-- VERIFICACIÓN FINAL
SELECT 'Roles'               AS Tabla, COUNT(*) AS Registros FROM Rol
UNION ALL SELECT 'Facultades',          COUNT(*) FROM Facultad
UNION ALL SELECT 'Programas',           COUNT(*) FROM Programa
UNION ALL SELECT 'Sedes',               COUNT(*) FROM Sede
UNION ALL SELECT 'Tipos de Espacio',    COUNT(*) FROM Tipo_Espacio
UNION ALL SELECT 'Espacios',            COUNT(*) FROM Espacio
UNION ALL SELECT 'Bloques Horarios',    COUNT(*) FROM Bloque_Horario
UNION ALL SELECT 'Usuarios',            COUNT(*) FROM Usuario
UNION ALL SELECT 'Categorias Equipo',   COUNT(*) FROM Categoria_Equipo
UNION ALL SELECT 'Equipos',             COUNT(*) FROM Equipo
UNION ALL SELECT 'Equipamiento Fijo',   COUNT(*) FROM Espacio_Equipamiento
UNION ALL SELECT 'Reservas',            COUNT(*) FROM Reserva
UNION ALL SELECT 'Reserva Bloques',     COUNT(*) FROM Reserva_Bloque
UNION ALL SELECT 'Incidentes',          COUNT(*) FROM Incidente
UNION ALL SELECT 'Requerimientos',      COUNT(*) FROM Detalle_Requerimiento
UNION ALL SELECT 'Ordenes Servicio',    COUNT(*) FROM Orden_Servicio;
