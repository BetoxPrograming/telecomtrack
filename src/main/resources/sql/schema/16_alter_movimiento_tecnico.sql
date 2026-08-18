USE telecomtrack;

-- ============================================================
-- Issue 16
-- Relaciona las salidas de materiales con el Técnico que los utiliza.
-- El campo responsable continúa registrando al Bodeguero que realizó
-- la operación de inventario.
-- ============================================================

ALTER TABLE movimiento
    ADD COLUMN id_tecnico INT NULL AFTER id_material;

ALTER TABLE movimiento
    ADD CONSTRAINT fk_movimiento_tecnico
        FOREIGN KEY (id_tecnico)
        REFERENCES usuario (id_usuario);
