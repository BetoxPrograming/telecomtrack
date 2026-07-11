USE telecomtrack;

CREATE TABLE IF NOT EXISTS asignacion_herramienta (
                                                      id_asignacion INT AUTO_INCREMENT,
                                                      id_herramienta INT NOT NULL,
                                                      id_tecnico INT NOT NULL,
                                                      id_proyecto INT NOT NULL,
                                                      fecha_asignacion DATE NOT NULL,
                                                      activa BOOLEAN NOT NULL DEFAULT TRUE,

                                                      PRIMARY KEY (id_asignacion),

    CONSTRAINT fk_asignacion_herramienta
    FOREIGN KEY (id_herramienta)
    REFERENCES herramienta (id_herramienta),

    CONSTRAINT fk_asignacion_tecnico
    FOREIGN KEY (id_tecnico)
    REFERENCES usuario (id_usuario),

    CONSTRAINT fk_asignacion_proyecto
    FOREIGN KEY (id_proyecto)
    REFERENCES proyecto (id_proyecto)
    );