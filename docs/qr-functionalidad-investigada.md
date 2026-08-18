# Funcionalidad investigada: código QR

La funcionalidad adicional investigada para TelecomTrack es la generación de códigos QR mediante **ZXing**. Esta librería no sustituye las tecnologías principales del curso; se integra al proyecto Spring Boot existente para facilitar la identificación de herramientas en campo.

## Alcance implementado

- Generación de un QR único por herramienta a partir de su identificador.
- El QR dirige a una ficha pública de la herramienta dentro de TelecomTrack.
- La ficha consulta la información actual desde la base de datos.
- Muestra nombre, código, estado actual, última asignación y devoluciones recientes.
- Renderiza el QR en PNG.
- Usa Bootstrap para que la consulta sea cómoda desde dispositivos móviles.

## Separación respecto a Firebase

**ZXing** corresponde a la funcionalidad investigada adicional del equipo.

**Firebase Storage no se presenta como investigación adicional**: se utiliza únicamente para las fotografías de devoluciones y sigue el mecanismo de almacenamiento de imágenes trabajado en la semana 4 del curso. La base de datos conserva las URL de las evidencias y no archivos locales ni imágenes en Base64.
