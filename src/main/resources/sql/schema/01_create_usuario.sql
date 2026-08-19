CREATE DATABASE IF NOT EXISTS telecomtrack
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE telecomtrack;

CREATE TABLE IF NOT EXISTS usuario (
                                       id_usuario INT AUTO_INCREMENT,
                                       nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuario_correo (correo)
    );