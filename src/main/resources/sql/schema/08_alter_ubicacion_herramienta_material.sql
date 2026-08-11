USE telecomtrack;

ALTER TABLE herramienta
    ADD COLUMN id_ubicacion INT NULL,
    ADD CONSTRAINT fk_herramienta_ubicacion
        FOREIGN KEY (id_ubicacion) REFERENCES ubicacion (id_ubicacion);

ALTER TABLE material
    ADD COLUMN id_ubicacion INT NULL,
    ADD CONSTRAINT fk_material_ubicacion
        FOREIGN KEY (id_ubicacion) REFERENCES ubicacion (id_ubicacion);

UPDATE herramienta
SET id_ubicacion = (SELECT id_ubicacion FROM ubicacion ORDER BY id_ubicacion LIMIT 1)
WHERE id_ubicacion IS NULL;

UPDATE material
SET id_ubicacion = (SELECT id_ubicacion FROM ubicacion ORDER BY id_ubicacion LIMIT 1)
WHERE id_ubicacion IS NULL;
