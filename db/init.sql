USE master;
GO;
DROP DATABASE IF EXISTS TU_BASE_DATOS;
GO;
CREATE DATABASE TU_BASE_DATOS COLLATE Modern_Spanish_100_CI_AI_SC;
GO;

-- Asegúrate de que no haya conexiones abiertas a la base de datos que deseas modificar
ALTER DATABASE TU_BASE_DATOS SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO


-- Cambia el collation de la base de datos
ALTER DATABASE TU_BASE_DATOS COLLATE Modern_Spanish_100_CI_AI_SC;
GO

-- Vuelve a permitir múltiples conexiones
ALTER DATABASE TU_BASE_DATOS SET MULTI_USER;
GO

USE TU_BASE_DATOS;
GO;

GO
;
CREATE SCHEMA inventario
GO;

CREATE TABLE inventario.fabricante
(
    id         INT PRIMARY KEY IDENTITY (1, 1),
    referencia NVARCHAR(32),
    CONSTRAINT UQ_fabricante UNIQUE (referencia)
);

CREATE TABLE inventario.contrato
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    referencia          NVARCHAR(256),
    fecha_compra        DATETIME2,                                                           -- Por simplificación, entendemos la fecha en la que nos entregan el equipo, más datos en el programa de gestión
    notas               NVARCHAR(1024),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    periodo_garantia    INT                DEFAULT 3 CHECK (periodo_garantia >= 0) NOT NULL, -- años
    CONSTRAINT UQ_contrato UNIQUE (referencia)
);

CREATE TABLE inventario.procesador
(
    id         INT PRIMARY KEY IDENTITY (1, 1),
    referencia NVARCHAR(128),
    fabricante INT not null,
    velocidad  INT, -- mhz
    nucleos    INT,
    hilos      INT,
    CONSTRAINT UQ_procesador UNIQUE (referencia),
    CONSTRAINT FK_procesador_fabricante FOREIGN KEY (fabricante) REFERENCES inventario.fabricante (id)
);

CREATE TABLE inventario.gpu
(
    id         INT PRIMARY KEY IDENTITY (1, 1),
    fabricante INT,
    referencia NVARCHAR(128), -- Limitaciones de PowerShell no detallar más.
    CONSTRAINT UQ_gpu UNIQUE (fabricante, referencia),
    CONSTRAINT FK_gpu_fabricante FOREIGN KEY (fabricante) REFERENCES inventario.fabricante (id)
);

CREATE TABLE inventario.modelo_ordenador
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    fabricante          INT       not null,
    referencia          NVARCHAR(64),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_modelo_pc_fabricante FOREIGN KEY (fabricante) REFERENCES inventario.fabricante (id),
    INDEX idx_modelo_pc_referencia (referencia),
    INDEX idx_modelo_pc_fabricante (fabricante)
);

CREATE TABLE inventario.sistema_operativo
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    nombre              NVARCHAR(64),
    compilacion         NVARCHAR(64),
    version             NVARCHAR(64),
    codename            NVARCHAR(64),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT sistema_operativo_unique UNIQUE (nombre, compilacion, version),
    INDEX idx_sistema_operativo_nombre_compilacion_version (nombre, compilacion, version),
    INDEX idx_sistema_operativo_codename (codename)
);

CREATE TABLE inventario.tipo_elemento
(
    id   INT PRIMARY KEY IDENTITY (1, 1),
    tipo NVARCHAR(64), -- Para discriminar servidor, ordenador...
    CONSTRAINT tipo_unique UNIQUE (tipo),
    INDEX idx_tipo (tipo)
);

INSERT INTO inventario.tipo_elemento (tipo)
VALUES ('ordenador'),
       ('servidor'),
       ('maquina_virtual'),
       ('impresora');

CREATE TABLE inventario.etiqueta
(
    id                 INT PRIMARY KEY IDENTITY (1, 1),
    referencia         NVARCHAR(256) NOT NULL,
    propuesto_retirada BIT default 0,
    CONSTRAINT UQ_pegatina_identificador UNIQUE (referencia),
    INDEX idx_pegatina_identificador (referencia)
);

CREATE TABLE inventario.ordenador
(
    id                  INT           NOT NULL PRIMARY KEY IDENTITY (1, 1),
    tipo_elemento       INT           NOT NULL, -- Servidor, ordenador...
    etiqueta            INT           NOT NULL,
    numero_serie        NVARCHAR(256) NOT NULL,
    modelo_ordenador    INT           not null,
    sistema_operativo   INT,
    ram                 BIGINT,                 -- Bytes
    contrato            INT,
    garantia            DATETIME2,
    propuesto_retirada  BIT,
    activo              BIT,                    -- Contemplar si está en almacén no activo, si es por XML si.
    notas               NVARCHAR(1024),
    fecha_alta          DATETIME2     not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_ordenador_etiqueta FOREIGN KEY (etiqueta) REFERENCES inventario.etiqueta (id),
    CONSTRAINT FK_ordenador_sistema_operativo FOREIGN KEY (sistema_operativo) REFERENCES inventario.sistema_operativo (id),
    CONSTRAINT FK_ordenador_modelo_ordenador FOREIGN KEY (modelo_ordenador)
        REFERENCES
            inventario
                .modelo_ordenador (id),
    CONSTRAINT FK_ordenador_contrato FOREIGN KEY (contrato) REFERENCES inventario.contrato (id),
    CONSTRAINT FK_tipo_elemento_ordenador FOREIGN KEY (tipo_elemento) REFERENCES inventario.tipo_elemento (id),
    INDEX idx_ordenador_numero_serie (numero_serie),
    INDEX idx_ordenador_etiqueta (etiqueta),
    INDEX idx_ordenador_modelo_ordenador (modelo_ordenador),
    INDEX idx_ordenador_sistema_operativo (sistema_operativo),
    INDEX idx_ordenador_tipo_elemento (tipo_elemento),
    INDEX idx_numero_serie (numero_serie),
    INDEX idx_etiqueta (etiqueta)
);

CREATE TABLE inventario.ordenador_gpu
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    ordenador           INT       not null,
    gpu                 INT       not null,
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_ordenador_gpu_ordenador FOREIGN KEY (ordenador) REFERENCES inventario.ordenador (id),
    CONSTRAINT FK_ordenador_gpu_gpu FOREIGN KEY (gpu) REFERENCES inventario.gpu (id),
    CONSTRAINT UQ_ordenador_gpu UNIQUE (ordenador, gpu),
    INDEX idx_ordenador_gpu_ordenador (ordenador),
);

CREATE TABLE inventario.ordenador_procesador
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    ordenador           INT       not null,
    procesador          INT       not null,
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_ordenador_procesador_ordenador FOREIGN KEY (ordenador) REFERENCES inventario.ordenador (id),
    CONSTRAINT FK_ordenador_procesador_procesador FOREIGN KEY (procesador) REFERENCES inventario.procesador (id),
    CONSTRAINT UQ_ordenador_procesador UNIQUE (ordenador, procesador),
    INDEX idx_ordenador_procesador_ordenador (ordenador),
);

CREATE TABLE inventario.ordenador_network
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    ordenador           INT       not null,
    ipv4                NVARCHAR(15),
    mac                 NCHAR(17),
    boca_red            NVARCHAR(64),
    notas               NVARCHAR(1024),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_ordenador_network_ordenador FOREIGN KEY (ordenador) REFERENCES inventario.ordenador (id),
    CONSTRAINT UQ_ordenador_network UNIQUE (ordenador, ipv4, mac),
    INDEX idx_ordenador_network_ordenador (ordenador)
);

CREATE TABLE inventario.modelo_monitor
(
    id         INT PRIMARY KEY IDENTITY (1, 1),
    referencia NVARCHAR(64),
    fabricante INT not null,
    CONSTRAINT FK_modelo_monitor_fabricante FOREIGN KEY (fabricante) REFERENCES inventario.fabricante (id),
    INDEX idx_modelo_monitor (referencia),
    INDEX idx_modelo_monitor_fabricante (fabricante)
)

CREATE TABLE inventario.monitor
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    etiqueta            INT,
    ordenador           INT,
    modelo_monitor      INT           not null,
    propuesto_retirada  BIT                    default 0,
    activo              BIT,
    numero_serie        NVARCHAR(256) NOT NULL,
    contrato            INT,
    garantia            DATETIME2,
    fecha_alta          DATETIME2     not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_monitor_etiqueta FOREIGN KEY (etiqueta) REFERENCES inventario.etiqueta (id),
    CONSTRAINT FK_monitor_etiqueta_ordenador FOREIGN KEY (ordenador) REFERENCES inventario.ordenador (id),
    CONSTRAINT FK_monitor_modelo_monitor FOREIGN KEY (modelo_monitor) REFERENCES inventario.modelo_monitor (id),
    CONSTRAINT FK_monitor_contrato FOREIGN KEY (contrato) REFERENCES inventario.contrato (id),
    INDEX idx_monitor_numero_serie (numero_serie),
    INDEX idx_monitor_etiqueta (etiqueta),
    INDEX idx_monitor_modelo_monitor (modelo_monitor),
);

CREATE TABLE inventario.ordenador_dicom
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    ordenador           INT       not null,
    ae_title_query      INT,
    ae_title_retrieve   INT,
    server_url          NVARCHAR(256),
    server_port         INT,
    notas               NVARCHAR(1024),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_dicom_configuracion_etiqueta FOREIGN KEY (ordenador) REFERENCES inventario.ordenador (id),
    CONSTRAINT UQ_dicom_configuracion UNIQUE (ae_title_query, ae_title_retrieve,
                                              server_url, server_port),
    INDEX idx_dicom_configuracion_ordenador (ordenador)
);

CREATE TABLE inventario.servicio
(
    id       INT PRIMARY KEY IDENTITY (1, 1),
    servicio NVARCHAR(128),
    CONSTRAINT servicio_unique UNIQUE (servicio)
);

INSERT INTO inventario.servicio (servicio)
VALUES ('almacen');

CREATE TABLE inventario.localizacion
(
    id           INT PRIMARY KEY IDENTITY (1, 1),
    localizacion NVARCHAR(128),
    CONSTRAINT localizacion_unique UNIQUE (localizacion)
);

CREATE TABLE inventario.trazabilidad
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    etiqueta            INT       not null,
    servicio            INT,            -- OU: RT, RM, RX, US, TC, MR, etc.
    localizacion        INT,            -- OU: Vigo, Ourense...
    notas               NVARCHAR(1024), -- FQDN CN OU
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_trazabilidad_servicio FOREIGN KEY (servicio) REFERENCES inventario.servicio (id),
    CONSTRAINT FK_trazabilidad_localizacion FOREIGN KEY (localizacion) REFERENCES inventario.localizacion (id),
    CONSTRAINT FK_trazabilidad_etiqueta FOREIGN KEY (etiqueta) REFERENCES inventario.etiqueta (id),
    INDEX idx_trazabilidad_etiqueta (etiqueta)
);

CREATE TABLE inventario.nivel
(
    id    INT PRIMARY KEY IDENTITY (1, 1),
    nivel NVARCHAR(32),
    CONSTRAINT nivel_unique UNIQUE (nivel)
);

INSERT INTO inventario.nivel (nivel)
VALUES ('ERROR'),
       ('INFO'),
       ('WARNING');

CREATE TABLE inventario.estado
(
    id     INT PRIMARY KEY IDENTITY (1, 1),
    estado NVARCHAR(32),
    CONSTRAINT estado_unique UNIQUE (estado)
);

INSERT INTO inventario.estado (estado)
VALUES ('Abierto'),
       ('En curso'),
       ('Cerrado'),
       ('Pendiente'),
       ('No se puede reproducir'),
       ('Rechazado'),
       ('Ajeno a GALARIA'),
       ('No se puede arreglar'),
       ('No se arreglará');

CREATE TABLE inventario.incidencia
(
    id                  INT PRIMARY KEY IDENTITY (1, 1),
    etiqueta            INT,
    estado              INT       not null,
    descripcion         NVARCHAR(max),
    fecha_alta          DATETIME2 not null DEFAULT GETDATE(),
    fecha_actualizacion DATETIME2,
    fecha_baja          DATETIME2,
    CONSTRAINT FK_incidencia_etiqueta FOREIGN KEY (etiqueta) REFERENCES inventario.etiqueta (id),
    CONSTRAINT FK_incidencia_estado FOREIGN KEY (estado) REFERENCES inventario.estado (id),
    INDEX idx_incidencia_etiqueta (etiqueta),
    INDEX idx_incidencia_estado (estado)
);

CREATE TABLE inventario.logs
(
    id          INT PRIMARY KEY IDENTITY (1, 1),
    etiqueta    INT,
    nivel       INT,
    fecha       DATETIME2 not null DEFAULT GETDATE(),
    descripcion NVARCHAR(1024),
    CONSTRAINT FK_logs_nivel FOREIGN KEY (nivel) REFERENCES inventario.nivel (id),
    CONSTRAINT FK_logs_etiqueta FOREIGN KEY (etiqueta) REFERENCES inventario.etiqueta (id),
    INDEX idx_logs_etiqueta (etiqueta),
    INDEX idx_logs_nivel (nivel)
);
GO;
DROP VIEW IF EXISTS inventario.vista_ordenadores
GO;
CREATE VIEW inventario.vista_ordenadores AS
WITH UltimaTrazabilidad AS (SELECT tr.etiqueta,
                                   s.servicio,
                                   l.localizacion,
                                   tr.fecha_alta                                                            AS fecha_trazabilidad,
                                   ROW_NUMBER() OVER (PARTITION BY tr.etiqueta ORDER BY tr.fecha_alta DESC) AS rn
                            FROM inventario.trazabilidad tr
                                     JOIN
                                 inventario.servicio s ON tr.servicio = s.id
                                     JOIN
                                 inventario.localizacion l ON tr.localizacion = l.id)
SELECT o.id                                        AS "ID",
       o.numero_serie                              AS "Número de Serie",
       e.referencia                                AS "Etiqueta",
       n.ipv4                                      AS "IP Primaria",
       m.referencia                                AS "Modelo",
       t.servicio                                  AS "Servicio",
       t.localizacion                              AS "Localización",
       c.referencia                                AS "Contrato",
       FORMAT(o.garantia, 'yyyy-MM-dd HH:mm')      AS "Garantía",
       o.activo                                    AS "Activo",
       o.propuesto_retirada                        AS "Propuesto Retirada",
       CONVERT(VARCHAR, o.fecha_alta, 20)          AS "Fecha de Alta",
       CONVERT(VARCHAR, o.fecha_actualizacion, 20) AS "Fecha de Actualización",
       CONVERT(VARCHAR, o.fecha_baja, 20)          AS "Fecha de Baja",
       CONCAT(so.nombre, ' ', so.codename)         AS "Sistema Operativo"
FROM inventario.ordenador o
         JOIN
     inventario.etiqueta e ON o.etiqueta = e.id
         JOIN
     inventario.modelo_ordenador m ON o.modelo_ordenador = m.id
         JOIN
     inventario.ordenador_network n ON o.id = n.ordenador
         LEFT JOIN
     inventario.sistema_operativo so ON o.sistema_operativo = so.id
         LEFT JOIN
     UltimaTrazabilidad t ON e.id = t.etiqueta AND t.rn = 1
         LEFT JOIN
     inventario.contrato c ON o.contrato = c.id
WHERE n.ipv4 LIKE '69%';
GO;
DROP VIEW IF EXISTS inventario.vista_monitores;
GO;
CREATE VIEW inventario.vista_monitores AS
WITH UltimaTrazabilidad AS (SELECT tr.etiqueta,
                                   s.servicio,
                                   l.localizacion,
                                   tr.fecha_alta                                                            AS fecha_trazabilidad,
                                   ROW_NUMBER() OVER (PARTITION BY tr.etiqueta ORDER BY tr.fecha_alta DESC) AS rn
                            FROM inventario.trazabilidad tr
                                     JOIN
                                 inventario.servicio s ON tr.servicio = s.id
                                     JOIN
                                 inventario.localizacion l ON tr.localizacion = l.id)
SELECT m.id                                        AS "ID",
       e.referencia                                AS "Etiqueta",
       o.numero_serie                              AS "Número de Serie",
       modelo.referencia                           AS "Modelo",
       t.servicio                                  AS "Servicio",
       t.localizacion                              AS "Localización",
       c.referencia                                AS "Contrato",
       FORMAT(m.garantia, 'yyyy-MM-dd HH:mm')      AS "Garantía",
       m.activo                                   AS "Activo",
         m.propuesto_retirada                        AS "Propuesto Retirada",
       CONVERT(VARCHAR, m.fecha_alta, 20)          AS "Fecha de Alta",
       CONVERT(VARCHAR, m.fecha_actualizacion, 20) AS "Fecha de Actualización",
       CONVERT(VARCHAR, m.fecha_baja, 20)          AS "Fecha de Baja"
FROM inventario.monitor m
         JOIN
     inventario.etiqueta e ON m.etiqueta = e.id
         JOIN
     inventario.modelo_monitor modelo ON m.modelo_monitor = modelo.id
         JOIN
     inventario.ordenador o ON m.ordenador = o.id
         LEFT JOIN
     UltimaTrazabilidad t ON e.id = t.etiqueta AND t.rn = 1
         LEFT JOIN
     inventario.contrato c ON m.contrato = c.id;
GO;
DROP VIEW IF EXISTS inventario.vista_incidencias;
go;
CREATE VIEW inventario.vista_incidencias AS
WITH UltimaTrazabilidad AS (SELECT tr.etiqueta,
                                   s.servicio,
                                   l.localizacion,
                                   tr.fecha_alta                                                            AS fecha_trazabilidad,
                                   ROW_NUMBER() OVER (PARTITION BY tr.etiqueta ORDER BY tr.fecha_alta DESC) AS rn
                            FROM inventario.trazabilidad tr
                                     JOIN
                                 inventario.servicio s ON tr.servicio = s.id
                                     JOIN
                                 inventario.localizacion l ON tr.localizacion = l.id)
SELECT i.id                                        AS "ID",
       e.referencia                                AS "Etiqueta",
       estado.estado                               AS "Estado",
       i.descripcion                               AS "Descripción",
       t.servicio                                  AS "Servicio",
       t.localizacion                              AS "Localización",
       FORMAT(i.fecha_alta, 'yyyy-MM-dd HH:mm')    AS "Fecha de Alta",
       CONVERT(VARCHAR, i.fecha_actualizacion, 20) AS "Fecha de Actualización",
       CONVERT(VARCHAR, i.fecha_baja, 20)          AS "Fecha de Baja"

FROM inventario.incidencia i
         JOIN
     inventario.etiqueta e ON i.etiqueta = e.id
         JOIN
     inventario.estado estado ON i.estado = estado.id
         LEFT JOIN
     UltimaTrazabilidad t ON e.id = t.etiqueta AND t.rn = 1;
GO;