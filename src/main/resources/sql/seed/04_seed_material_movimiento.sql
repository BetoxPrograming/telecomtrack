USE telecomtrack;

INSERT INTO categoria (nombre, tipo) VALUES
    ('Cables y Conectores', 'Infraestructura'),
    ('Herramientas', 'Equipamiento'),
    ('Equipos de Red', 'Infraestructura');

INSERT INTO proveedor (nombre, contacto, correo) VALUES
    ('Distribuidora TeleCR', 'Juan Mora', 'ventas@telecr.com'),
    ('ImportaRed S.A.', 'María Solano', 'pedidos@importared.com'),
    ('TechSupply Costa Rica', 'Carlos Jiménez', 'info@techsupply.cr');

INSERT INTO material (codigo_unico, nombre, descripcion, unidad_medida, stock_actual, stock_minimo, id_categoria, id_proveedor) VALUES
    ('CAB-UTP-001', 'Cable UTP Cat6', 'Cable de red categoría 6 para instalaciones internas', 'metros', 0, 50, 1, 1),
    ('CON-RJ45-001', 'Conector RJ45', 'Conector RJ45 macho para cable UTP', 'unidades', 0, 100, 1, 1),
    ('HER-CRI-001', 'Crimpadora RJ45', 'Herramienta para crimpar conectores RJ45', 'unidades', 0, 2, 2, 2),
    ('EQP-SWT-001', 'Switch 8 puertos', 'Switch no administrable de 8 puertos Gigabit', 'unidades', 0, 3, 3, 3),
    ('CAB-FIB-001', 'Cable fibra óptica', 'Cable de fibra óptica monomodo 9/125', 'metros', 0, 20, 1, 2);

INSERT INTO movimiento (tipo, cantidad, fecha, observacion, responsable, id_material, id_proveedor) VALUES
    ('ENTRADA', 200, NOW(), 'Pedido inicial de instalación', 'Ana Rodríguez', 1, 1),
    ('ENTRADA', 500, NOW(), 'Stock inicial conectores', 'Carlos Mora', 2, 1),
    ('ENTRADA', 5, NOW(), 'Herramientas taller', 'Ana Rodríguez', 3, 2);

UPDATE material SET stock_actual = 200 WHERE codigo_unico = 'CAB-UTP-001';
UPDATE material SET stock_actual = 500 WHERE codigo_unico = 'CON-RJ45-001';
UPDATE material SET stock_actual = 5 WHERE codigo_unico = 'HER-CRI-001';
