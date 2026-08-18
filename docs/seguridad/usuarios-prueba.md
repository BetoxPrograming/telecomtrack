# Usuarios de prueba por rol

Todos los usuarios de demostración usan la contraseña `Telecom123*`. En base de datos se almacena únicamente el hash BCrypt.

| Rol | Correo | Estado | Prueba principal |
|---|---|---|---|
| Administrador | ana.rodriguez@telecomtrack.com | Activo | Acceso a usuarios y a todos los módulos |
| Bodeguero | carlos.mora@telecomtrack.com | Activo | Inventario, solicitudes pendientes, dashboard e importación CSV |
| Técnico | luis.vargas@telecomtrack.com | Activo | Nueva solicitud y sus propias solicitudes |
| Supervisor | maria.fernandez@telecomtrack.com | Activo | Proyectos, listados estimados, reportes y dashboard de supervisor |
| Visitante | visitante@telecomtrack.com | Activo | Acceso autenticado básico y catálogo público |
| Técnico inactivo | jose.castro@telecomtrack.com | Inactivo | Debe rechazarse el inicio de sesión |

## Pruebas recomendadas

1. Iniciar sesión con cada usuario activo y comprobar que solo aparezcan las opciones autorizadas.
2. Intentar abrir directamente una ruta de otro rol y comprobar que se muestre acceso denegado.
3. Iniciar sesión con `jose.castro@telecomtrack.com` y comprobar que Spring Security rechace el acceso.
4. Cerrar sesión y comprobar que las rutas protegidas soliciten autenticación.
5. Abrir `/herramienta/catalogo` sin sesión y comprobar que el catálogo público siga disponible.

> Estas credenciales son únicamente para demostración académica. No deben utilizarse en producción.
