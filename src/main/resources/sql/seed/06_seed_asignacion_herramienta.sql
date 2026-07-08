USE telecomtrack;

SET @id_herramienta = (
    SELECT id_herramienta
    FROM herramienta
    WHERE estado = 'Disponible'
    ORDER BY id_herramienta
    LIMIT 1
);

SET @id_tecnico = (
    SELECT id_usuario
    FROM usuario
    WHERE activo = TRUE
      AND rol = 'Técnico'
    ORDER BY id_usuario
    LIMIT 1
);

SET @id_proyecto = (
    SELECT id_proyecto
    FROM proyecto
    WHERE estado = 'Activo'
    ORDER BY id_proyecto
    LIMIT 1
);

INSERT INTO asignacion_herramienta (
    id_herramienta,
    id_tecnico,
    id_proyecto,
    fecha_asignacion,
    activa
)
SELECT
    @id_herramienta,
    @id_tecnico,
    @id_proyecto,
    CURDATE(),
    TRUE
    WHERE @id_herramienta IS NOT NULL
  AND @id_tecnico IS NOT NULL
  AND @id_proyecto IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM asignacion_herramienta
      WHERE id_herramienta = @id_herramienta
        AND activa = TRUE
  );

UPDATE herramienta h
    INNER JOIN asignacion_herramienta a
ON a.id_herramienta = h.id_herramienta
    SET h.estado = 'Asignada'
WHERE h.id_herramienta = @id_herramienta
  AND a.activa = TRUE;