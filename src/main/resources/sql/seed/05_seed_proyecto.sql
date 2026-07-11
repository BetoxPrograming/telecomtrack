USE telecomtrack;

INSERT INTO proyecto (
    nombre,
    descripcion,
    fecha_inicio,
    fecha_fin_estimada,
    estado,
    id_supervisor
)
VALUES
    (
        'Instalación de red empresarial',
        'Instalación de cableado estructurado y equipos de telecomunicaciones.',
        '2026-07-01',
        '2026-08-15',
        'Activo',
        (
            SELECT id_usuario
            FROM usuario
            WHERE correo = 'maria.fernandez@telecomtrack.com'
            LIMIT 1
    )
    ),
(
    'Mantenimiento de torre principal',
    'Mantenimiento preventivo de infraestructura y equipos instalados.',
    '2026-07-10',
    '2026-09-01',
    'Activo',
    (
        SELECT id_usuario
        FROM usuario
        WHERE correo = 'maria.fernandez@telecomtrack.com'
        LIMIT 1
    )
),
(
    'Actualización de enlaces secundarios',
    'Actualización de enlaces y revisión de equipos secundarios.',
    '2026-05-01',
    '2026-06-30',
    'Finalizado',
    (
        SELECT id_usuario
        FROM usuario
        WHERE correo = 'maria.fernandez@telecomtrack.com'
        LIMIT 1
    )
);