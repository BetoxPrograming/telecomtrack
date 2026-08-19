USE telecomtrack;

INSERT INTO herramienta (
    codigo,
    nombre,
    categoria,
    descripcion,
    estado,
    fecha_retorno_estimada,
    fecha_baja_definitiva,
    justificacion_baja_definitiva,
    id_ubicacion
) VALUES
    (
        'H-001',
        'Taladro inalámbrico',
        'Perforación',
        'Taladro para trabajos de instalación y perforación ligera.',
        'Disponible',
        NULL,
        NULL,
        NULL,
        (SELECT id_ubicacion FROM ubicacion WHERE nombre = 'Bodega Central' LIMIT 1)
    ),
    (
        'H-002',
        'Multímetro digital',
        'Medición',
        'Instrumento para medición eléctrica de uso general.',
        'Mantenimiento',
        '2026-07-20',
        NULL,
        NULL,
        (SELECT id_ubicacion FROM ubicacion WHERE nombre = 'Bodega Central' LIMIT 1)
    ),
    (
        'H-003',
        'Escalera telescópica',
        'Altura',
        'Escalera liviana para trabajos en altura.',
        'Baja',
        NULL,
        '2026-07-01',
        'Presentó daño estructural y salió de servicio.',
        (SELECT id_ubicacion FROM ubicacion WHERE nombre = 'Bodega Central' LIMIT 1)
    );