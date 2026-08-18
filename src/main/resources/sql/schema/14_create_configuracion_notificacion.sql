USE telecomtrack;

CREATE TABLE IF NOT EXISTS configuracion_notificacion (
    id_configuracion INT NOT NULL AUTO_INCREMENT,
    notificar_stock_minimo BOOLEAN NOT NULL DEFAULT TRUE,
    notificar_solicitudes_pendientes BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_configuracion)
) ENGINE = InnoDB;
