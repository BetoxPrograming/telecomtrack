USE telecomtrack;

CREATE TABLE IF NOT EXISTS herramienta (
                                         id_herramienta INT AUTO_INCREMENT,
                                         codigo VARCHAR(50) NOT NULL,
                                         nombre VARCHAR(100) NOT NULL,
                                          descripcion VARCHAR(255),
                                          estado VARCHAR(20) NOT NULL DEFAULT 'Disponible',
                                          fecha_retorno_estimada DATE DEFAULT NULL,
                                          fecha_baja_definitiva DATE DEFAULT NULL,
                                          justificacion_baja_definitiva VARCHAR(255) DEFAULT NULL,
                                         PRIMARY KEY (id_herramienta),
                                         UNIQUE KEY uk_herramienta_codigo (codigo)
    );
