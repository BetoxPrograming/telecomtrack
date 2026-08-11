USE telecomtrack;

CREATE TABLE IF NOT EXISTS listado_material_estimado (
    id_listado INT AUTO_INCREMENT,
    id_proyecto INT NOT NULL,
    fecha_creacion DATE NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    comentario_supervisor VARCHAR(255),
    id_usuario_decision INT,
    fecha_decision DATE,

    PRIMARY KEY (id_listado),

    CONSTRAINT fk_listado_proyecto
        FOREIGN KEY (id_proyecto) REFERENCES proyecto (id_proyecto),

    CONSTRAINT fk_listado_usuario_decision
        FOREIGN KEY (id_usuario_decision) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS detalle_material_estimado (
    id_detalle INT AUTO_INCREMENT,
    id_listado INT NOT NULL,
    id_material BIGINT NOT NULL,
    cantidad_estimada INT NOT NULL,

    PRIMARY KEY (id_detalle),

    CONSTRAINT fk_detalle_estimado_listado
        FOREIGN KEY (id_listado) REFERENCES listado_material_estimado (id_listado),

    CONSTRAINT fk_detalle_estimado_material
        FOREIGN KEY (id_material) REFERENCES material (id_material)
);
