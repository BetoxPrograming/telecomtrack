USE telecomtrack;

-- ============================================================
-- Issue 14 - Tarea 1
-- Modelo base de roles, relación usuario_rol y rutas de seguridad.
-- La autenticación se incorpora en una tarea posterior.
-- ============================================================

CREATE TABLE IF NOT EXISTS rol (
    id_rol INT NOT NULL AUTO_INCREMENT,
    rol VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_rol),
    UNIQUE KEY uk_rol_rol (rol)
);

CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE IF NOT EXISTS ruta (
    id_ruta INT NOT NULL AUTO_INCREMENT,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_ruta),
    CONSTRAINT fk_ruta_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
    CONSTRAINT chk_ruta_rol
        CHECK (id_rol IS NOT NULL OR requiere_rol = FALSE)
);

ALTER TABLE usuario
    ADD COLUMN password VARCHAR(255) NULL AFTER correo;
