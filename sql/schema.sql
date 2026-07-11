-- ============================================================
-- TelecomTrack — Módulo: Materiales y Control de Stock
-- Script SCHEMA — crea la estructura de la base de datos
-- HU-02 (materiales) | HU-03 (entradas) | HU-09 (alertas de stock mínimo)
-- Ejecutar PRIMERO, antes de seed.sql (desde MySQL Workbench).
-- ============================================================

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80)  NOT NULL,
    tipo         VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    contacto     VARCHAR(100),
    correo       VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS material (
    id_material   BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_unico  VARCHAR(50)  NOT NULL,
    nombre        VARCHAR(100) NOT NULL,
    unidad_medida VARCHAR(30)  NOT NULL,
    stock_actual  INT          NOT NULL DEFAULT 0,
    stock_minimo  INT          NOT NULL DEFAULT 0,
    id_categoria  BIGINT,
    id_proveedor  BIGINT,
    CONSTRAINT uq_material_codigo UNIQUE (codigo_unico),
    CONSTRAINT fk_mat_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    CONSTRAINT fk_mat_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE IF NOT EXISTS movimiento (
    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo          VARCHAR(20)  NOT NULL,
    cantidad      INT          NOT NULL,
    fecha         DATETIME     NOT NULL,
    observacion   VARCHAR(255),
    responsable   VARCHAR(100),
    id_material   BIGINT,
    CONSTRAINT fk_mov_material FOREIGN KEY (id_material) REFERENCES material(id_material)
);
