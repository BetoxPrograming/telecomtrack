USE telecomtrack;

CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud INT AUTO_INCREMENT,
    id_tecnico INT NOT NULL,
    id_proyecto INT NOT NULL,
    id_ubicacion INT NOT NULL,
    fecha_solicitud DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo VARCHAR(255),
    id_usuario_decision INT,
    fecha_decision DATETIME,

    PRIMARY KEY (id_solicitud),

    CONSTRAINT fk_solicitud_tecnico
        FOREIGN KEY (id_tecnico) REFERENCES usuario (id_usuario),

    CONSTRAINT fk_solicitud_proyecto
        FOREIGN KEY (id_proyecto) REFERENCES proyecto (id_proyecto),

    CONSTRAINT fk_solicitud_ubicacion
        FOREIGN KEY (id_ubicacion) REFERENCES ubicacion (id_ubicacion),

    CONSTRAINT fk_solicitud_usuario_decision
        FOREIGN KEY (id_usuario_decision) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS detalle_solicitud (
    id_detalle INT AUTO_INCREMENT,
    id_solicitud INT NOT NULL,
    id_herramienta INT,
    id_material BIGINT,
    cantidad INT,

    PRIMARY KEY (id_detalle),

    CONSTRAINT fk_detalle_solicitud
        FOREIGN KEY (id_solicitud) REFERENCES solicitud (id_solicitud),

    CONSTRAINT fk_detalle_herramienta
        FOREIGN KEY (id_herramienta) REFERENCES herramienta (id_herramienta),

    CONSTRAINT fk_detalle_material
        FOREIGN KEY (id_material) REFERENCES material (id_material)
);
