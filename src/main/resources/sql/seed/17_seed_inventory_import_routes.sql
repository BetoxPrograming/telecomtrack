USE telecomtrack;

-- ============================================================
-- Issue 14 - Tarea 6
-- Autoriza la pantalla de importación CSV dentro del módulo
-- de inventario. Administrador también accede porque posee
-- el rol Bodeguero según la configuración del proyecto.
-- ============================================================

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/inventario/importar', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/inventario/importar');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/inventario/importar/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/inventario/importar/**');
