-- ============================================================
-- TelecomTrack — Módulo: Materiales y Control de Stock
-- Script SEED — inserta datos de prueba
-- Ejecutar DESPUÉS de schema.sql (desde MySQL Workbench).
-- Permite comprobar: listado, selectores de categoría/proveedor,
-- alertas de stock mínimo, historial de entradas y trazabilidad del responsable.
-- ============================================================

-- Categorías disponibles
INSERT INTO categoria (nombre, tipo) VALUES
    ('Cableado',        'Material'),
    ('Conectores',      'Material'),
    ('Herramientas',    'Herramienta'),
    ('Equipos activos', 'Equipo');

-- Proveedores
INSERT INTO proveedor (nombre, contacto, correo) VALUES
    ('Distribuidora TEC', 'Juan Mora',   'jmora@dtec.cr'),
    ('CableCR S.A.',      'Ana Solís',   'asolis@cablecr.com'),
    ('ElectroInsumos',    'Pedro Rojas', 'projas@electroinsumos.cr');

-- Materiales:
--   * Con stock normal:  CAB-UTP-001, CON-RJ45-001, MAT-CEL-001, EQ-SW-001
--   * Con stock igual o inferior al mínimo (dispara la alerta HU-09):
--        HER-PEL-001 (stock 5 = mínimo 5) y HER-CRI-001 (stock 2 < mínimo 3)
INSERT INTO material (codigo_unico, nombre, unidad_medida, stock_actual, stock_minimo, id_categoria, id_proveedor) VALUES
    ('CAB-UTP-001', 'Cable UTP Cat6',        'metro',  500, 100, 1, 2),
    ('CON-RJ45-001','Conector RJ-45',        'unidad', 200,  50, 2, 2),
    ('HER-PEL-001', 'Pelacables',            'unidad',   5,   5, 3, 1),
    ('HER-CRI-001', 'Crimpeadora RJ-45',     'unidad',   2,   3, 3, 1),
    ('MAT-CEL-001', 'Cintillo plástico 20cm','bolsa',   30,  10, 1, 3),
    ('EQ-SW-001',   'Switch 8p no manejable','unidad',  10,   1, 4, 3);

-- Movimientos de entrada asociados con materiales y responsables (trazabilidad)
INSERT INTO movimiento (tipo, cantidad, fecha, observacion, responsable, id_material) VALUES
    ('ENTRADA', 500, NOW(), 'Compra inicial — Orden #2024-001', 'Juan Mora',   1),
    ('ENTRADA', 200, NOW(), 'Compra inicial — Orden #2024-001', 'Ana Solís',   2),
    ('ENTRADA',   5, NOW(), 'Dotación herramientas',            'Pedro Rojas', 3),
    ('ENTRADA',   2, NOW(), 'Dotación herramientas',            'Pedro Rojas', 4),
    ('ENTRADA',  30, NOW(), 'Stock inicial',                    'Juan Mora',   5),
    ('ENTRADA',  10, NOW(), 'Compra inicial',                   'Ana Solís',   6);
