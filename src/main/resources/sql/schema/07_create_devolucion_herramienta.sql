USE telecomtrack;

CREATE TABLE IF NOT EXISTS devolucion_herramienta (
                                                      id_devolucion INT AUTO_INCREMENT,
                                                      id_asignacion INT NOT NULL,
                                                      fecha_devolucion DATE NOT NULL,
                                                      estado_devolucion VARCHAR(20) NOT NULL,
    ruta_foto VARCHAR(500),

    PRIMARY KEY (id_devolucion),

    CONSTRAINT uk_devolucion_asignacion
    UNIQUE (id_asignacion),

    CONSTRAINT fk_devolucion_asignacion
    FOREIGN KEY (id_asignacion)
    REFERENCES asignacion_herramienta (id_asignacion)
    );