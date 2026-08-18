USE telecomtrack;

-- ============================================================
-- Issue 14 - Tarea 5
-- Usuarios de prueba por rol para demostración y defensa.
--
-- Contraseña común de demostración: Telecom123*
-- El valor almacenado es BCrypt.
-- ============================================================

SET @demo_password = '$2y$10$a1qasBLLfiBsOEOEMIhBO.aN4JXAh33tm3g8EBYJdGFavwe/eEfwm';

-- Se reutilizan los usuarios de prueba originales para conservar
-- sus relaciones con proyectos, solicitudes y movimientos.
UPDATE usuario
SET password = @demo_password, rol = 'Administrador', activo = TRUE
WHERE correo = 'ana.rodriguez@telecomtrack.com';

UPDATE usuario
SET password = @demo_password, rol = 'Bodeguero', activo = TRUE
WHERE correo = 'carlos.mora@telecomtrack.com';

UPDATE usuario
SET password = @demo_password, rol = 'Técnico', activo = TRUE
WHERE correo = 'luis.vargas@telecomtrack.com';

UPDATE usuario
SET password = @demo_password, rol = 'Supervisor', activo = TRUE
WHERE correo = 'maria.fernandez@telecomtrack.com';

UPDATE usuario
SET password = @demo_password, rol = 'Técnico', activo = FALSE
WHERE correo = 'jose.castro@telecomtrack.com';

-- HU-10 contempla también el rol Visitante.
INSERT IGNORE INTO usuario (nombre, apellido, correo, password, rol, activo)
VALUES ('Paula', 'Visitante', 'visitante@telecomtrack.com', @demo_password, 'Visitante', TRUE);

UPDATE usuario
SET password = @demo_password, rol = 'Visitante', activo = TRUE
WHERE correo = 'visitante@telecomtrack.com';

-- Sincroniza la relación usuario_rol para los roles principales.
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
INNER JOIN rol r ON r.rol = u.rol
WHERE u.correo IN (
    'ana.rodriguez@telecomtrack.com',
    'carlos.mora@telecomtrack.com',
    'luis.vargas@telecomtrack.com',
    'maria.fernandez@telecomtrack.com',
    'jose.castro@telecomtrack.com',
    'visitante@telecomtrack.com'
);

-- El Administrador recibe todos los roles funcionales, como en la Tarea 3.
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
CROSS JOIN rol r
WHERE u.correo = 'ana.rodriguez@telecomtrack.com';
