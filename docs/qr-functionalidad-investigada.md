# Funcionalidad investigada: código QR

Se investigó la integración de códigos QR para cada herramienta usando la librería ZXing.

## Alcance implementado
- Generación de un QR único por herramienta.
- Acceso a una ficha pública con la información actualizada de la herramienta.
- Renderizado del QR en formato PNG.
- Vista responsiva con Bootstrap para facilitar consulta desde móviles.

## Observaciones
- El QR apunta a la ficha de la herramienta dentro de la aplicación.
- La información mostrada depende del estado y datos actuales almacenados en base de datos.
- La solución se integró respetando el estilo y la estructura existente del proyecto.
