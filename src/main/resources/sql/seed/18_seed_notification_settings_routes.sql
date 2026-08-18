USE telecomtrack;

-- Configuración inicial. El Administrador puede modificar ambos valores desde la aplicación.
INSERT INTO configuracion_notificacion (
    id_configuracion,
    notificar_stock_minimo,
    notificar_solicitudes_pendientes
)
SELECT 1, TRUE, TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM configuracion_notificacion
    WHERE id_configuracion = 1
);

-- La configuración de notificaciones es exclusiva del Administrador.
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/notificacion/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Administrador'
  AND NOT EXISTS (
      SELECT 1
      FROM ruta rt
      JOIN rol rr ON rt.id_rol = rr.id_rol
      WHERE rt.ruta = '/notificacion/**'
        AND rr.rol = 'Administrador'
  );
