USE telecomtrack;

-- Roles definidos en HU-10.
INSERT IGNORE INTO rol (rol) VALUES
    ('Administrador'),
    ('Bodeguero'),
    ('Técnico'),
    ('Supervisor'),
    ('Visitante');

-- Migra los roles que ya existen en el CRUD del Issue 2 hacia usuario_rol.
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u
INNER JOIN rol r ON r.rol = u.rol;
