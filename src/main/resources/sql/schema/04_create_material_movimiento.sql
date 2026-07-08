USE telecomtrack;

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria BIGINT AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    tipo VARCHAR(50),
    PRIMARY KEY (id_categoria)
);

CREATE TABLE IF NOT EXISTS proveedor (
    id_proveedor BIGINT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    correo VARCHAR(100),
    PRIMARY KEY (id_proveedor)
);

CREATE TABLE IF NOT EXISTS material (
    id_material BIGINT AUTO_INCREMENT,
    codigo_unico VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    unidad_medida VARCHAR(30) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    id_categoria BIGINT,
    id_proveedor BIGINT,
    PRIMARY KEY (id_material),
    UNIQUE KEY uk_material_codigo (codigo_unico),
    CONSTRAINT fk_material_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria),
    CONSTRAINT fk_material_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
);

CREATE TABLE IF NOT EXISTS movimiento (
    id_movimiento BIGINT AUTO_INCREMENT,
    tipo VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    fecha DATETIME NOT NULL,
    observacion VARCHAR(255),
    responsable VARCHAR(100) NOT NULL,
    id_material BIGINT NOT NULL,
    id_proveedor BIGINT NOT NULL,
    PRIMARY KEY (id_movimiento),
    CONSTRAINT fk_movimiento_material FOREIGN KEY (id_material) REFERENCES material (id_material),
    CONSTRAINT fk_movimiento_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
);
