# Firebase Storage para evidencias de devoluciones

Las fotografías de las devoluciones se almacenan en **Firebase Storage**, siguiendo el mecanismo trabajado en la semana 4 del curso. La base de datos guarda únicamente la URL de cada fotografía.

## Configuración

El proyecto no incluye credenciales privadas de Firebase. Para habilitar la carga de fotografías se deben definir estas variables de entorno:

```text
FIREBASE_ENABLED=true
FIREBASE_BUCKET_NAME=<bucket de Firebase Storage>
FIREBASE_STORAGE_PATH=telecomtrack
FIREBASE_JSON_PATH=firebase
FIREBASE_JSON_FILE=<nombre del archivo JSON privado>
```

El archivo JSON privado debe colocarse localmente en:

```text
src/main/resources/firebase/
```

La carpeta está ignorada por Git y las credenciales no deben subirse al repositorio.

Si `FIREBASE_ENABLED=false`, la aplicación puede iniciar sin Firebase configurado, pero no será posible registrar una devolución que intente cargar fotografías.

## Evidencias de devolución

- Máximo 3 fotografías por devolución.
- Formatos permitidos: JPG/JPEG y PNG.
- Tamaño máximo: 5 MB por fotografía.
- MySQL almacena la URL; el archivo se conserva en Firebase Storage.
