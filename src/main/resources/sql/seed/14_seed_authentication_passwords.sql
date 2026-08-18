USE telecomtrack;

-- ============================================================
-- Issue 14 - Tarea 2
-- Contraseña inicial cifrada para los usuarios ya existentes.
--
-- Contraseña temporal para pruebas locales:
-- Telecom123*
--
-- El valor almacenado en la base de datos es BCrypt.
-- Los usuarios nuevos se cifran desde UsuarioService.
-- ============================================================

UPDATE usuario
SET password = '$2y$10$a1qasBLLfiBsOEOEMIhBO.aN4JXAh33tm3g8EBYJdGFavwe/eEfwm'
WHERE password IS NULL OR password = '';
