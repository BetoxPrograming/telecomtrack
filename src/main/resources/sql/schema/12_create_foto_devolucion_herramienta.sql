USE telecomtrack;

CREATE TABLE IF NOT EXISTS foto_devolucion_herramienta (
    id_foto_devolucion INT AUTO_INCREMENT,
    id_devolucion INT NOT NULL,
    ruta_imagen VARCHAR(500) NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    fecha_carga DATETIME NOT NULL,
    PRIMARY KEY (id_foto_devolucion),
    CONSTRAINT fk_foto_devolucion
        FOREIGN KEY (id_devolucion)
        REFERENCES devolucion_herramienta (id_devolucion)
);
