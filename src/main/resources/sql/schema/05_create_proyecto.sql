USE telecomtrack;

CREATE TABLE IF NOT EXISTS proyecto (
                                        id_proyecto INT AUTO_INCREMENT,
                                        nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    fecha_inicio DATE NOT NULL,
    fecha_fin_estimada DATE,
    estado VARCHAR(20) NOT NULL,
    id_supervisor INT NOT NULL,

    PRIMARY KEY (id_proyecto),

    CONSTRAINT fk_proyecto_supervisor
    FOREIGN KEY (id_supervisor)
    REFERENCES usuario (id_usuario)
    );