USE telecomtrack;

-- ============================================================
-- Issue 14 - Tarea 3
-- Rutas públicas y rutas protegidas según los roles de TelecomTrack.
-- El orden es importante: las rutas específicas se registran antes
-- de los patrones generales que podrían contenerlas.
-- ============================================================

-- El Administrador conserva su rol principal y además recibe los demás
-- roles funcionales, siguiendo el modelo usuario_rol trabajado en Tienda.
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
CROSS JOIN rol r
WHERE u.rol = 'Administrador';

-- -----------------------------
-- Rutas públicas
-- -----------------------------
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/login', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/login');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/acceso_denegado', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/acceso_denegado');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/error', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/error');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/herramienta/catalogo', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/herramienta/catalogo');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/webjars/**', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/webjars/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/js/**', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/js/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/css/**', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/css/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/favicon.ico', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/favicon.ico');

-- -----------------------------
-- Administrador
-- -----------------------------
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/usuario/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Administrador'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/usuario/**');

-- -----------------------------
-- Bodeguero
-- -----------------------------
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/ubicacion/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/ubicacion/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/materiales', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/materiales');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/materiales/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/materiales/**');

-- El catálogo público ya fue registrado antes de este patrón general.
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/herramienta/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/herramienta/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/asignacion/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/asignacion/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/pendientes', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/pendientes');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/aprobar/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/aprobar/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/rechazar/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/rechazar/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/dashboard', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Bodeguero'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/dashboard');

-- -----------------------------
-- Técnico
-- -----------------------------
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/nueva', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/nueva');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/guardar', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/guardar');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/solicitud/mis-solicitudes', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/solicitud/mis-solicitudes');

-- /solicitud/consultar/** queda para cualquier usuario autenticado por ahora,
-- porque la misma vista es utilizada por Técnico y Bodeguero. En la Tarea 4
-- se limitará el contenido según el usuario autenticado.

-- -----------------------------
-- Supervisor
-- -----------------------------
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/proyecto/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Supervisor'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/proyecto/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/listado-material-estimado/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Supervisor'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/listado-material-estimado/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/reportes/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Supervisor'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/reportes/**');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/dashboard/supervisor', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Supervisor'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/dashboard/supervisor');
