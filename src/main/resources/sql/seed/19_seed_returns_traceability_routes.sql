USE telecomtrack;

-- ============================================================
-- Issue 16
-- Rutas de devoluciones, historial del técnico y consulta QR.
-- ============================================================

-- QR: consulta pública desde dispositivos móviles.
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/qr/**', NULL, FALSE
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/qr/**');

-- Técnico: devoluciones e historial personal de herramientas.
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/devolucion/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/devolucion/**');

-- Técnico: historial de materiales consumidos desde movimientos de salida.
-- Se registra la ruta exacta porque el controlador responde directamente en
-- /historial-material; el patrón adicional cubre futuras subrutas del módulo.
INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/historial-material', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/historial-material');

INSERT INTO ruta (ruta, id_rol, requiere_rol)
SELECT '/historial-material/**', r.id_rol, TRUE
FROM rol r
WHERE r.rol = 'Técnico'
  AND NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = '/historial-material/**');
