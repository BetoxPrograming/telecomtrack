# 📡 TelecomTrack

> **Sistema web transaccional para Telecom Power Systems**  
> Centraliza la gestión de herramientas, materiales, proyectos, solicitudes, asignaciones, devoluciones y trazabilidad del inventario.

TelecomTrack corresponde al proyecto final del curso **SC-403 Desarrollo de Aplicaciones Web y Patrones** de la **Universidad Fidélitas**.

---

## ✅ Estado del proyecto

| Entrega | Estado |
|---|---|
| Proyecto final | ✅ Finalizado |
| Versión | Semana 15 |
| Rama estable | `main` |
| Arquitectura | MVC |

> [!NOTE]
> La aplicación integra los módulos funcionales definidos para el proyecto, autenticación y autorización por roles, persistencia mediante MySQL/JPA, internacionalización, almacenamiento de evidencias en Firebase Storage, notificaciones por correo, reportes exportables y una funcionalidad adicional investigada basada en códigos QR.

---

## 📋 Módulos implementados

| Módulo | Funcionalidad principal |
|---|---|
| 🔐 Autenticación y seguridad | Inicio y cierre de sesión, BCrypt, usuarios activos/inactivos y control de acceso mediante Spring Security. |
| 👥 Usuarios y roles | Administración de usuarios con roles Administrador, Bodeguero, Técnico, Supervisor y Visitante. |
| 📍 Ubicaciones | Registro y mantenimiento de bodegas o sitios asociados al inventario. |
| 🧰 Herramientas | CRUD, categoría, ubicación, estado, valor unitario, mantenimiento, baja y catálogo público. |
| 📦 Materiales | CRUD, stock actual, stock mínimo, valor unitario, entradas y movimientos de inventario. |
| 📥 Importación de inventario | Importación inicial de herramientas y materiales mediante archivos CSV con validación por fila. |
| 🏗️ Proyectos | Registro de proyectos y asignación de Supervisor. |
| 📝 Listados estimados | Definición de materiales estimados por proyecto y gestión de su estado. |
| 📤 Solicitudes | Solicitudes de herramientas y materiales realizadas por Técnicos y atendidas por Bodega. |
| 🔧 Asignaciones | Registro de herramientas asignadas a Técnicos y proyectos. |
| ↩️ Devoluciones | Registro del estado de devolución, mantenimiento, baja y evidencias fotográficas. |
| 🔎 Trazabilidad | Historial de herramientas y materiales asociado al Técnico y a los movimientos registrados. |
| 📊 Dashboard | Indicadores para Administrador y métricas del Supervisor limitadas a sus proyectos. |
| 📑 Reportes | Inventario, consumo por proyecto y activos por Técnico, exportables a Excel y PDF. |
| 📧 Notificaciones | Configuración y envío de alertas de solicitudes pendientes y stock mínimo mediante Spring Mail. |
| 🌐 Internacionalización | Interfaz disponible en español, inglés, japonés y chino mediante archivos de idioma. |
| 🔳 Código QR | Generación de QR por herramienta y ficha pública con información actual consultada desde la base de datos. |

---

## 🛠️ Tecnologías utilizadas

| Área | Tecnología |
|---|---|
| ☕ Lenguaje | Java 21 |
| 🌱 Framework | Spring Boot 4.0.6 |
| 🧩 Arquitectura | Modelo MVC |
| 🖥️ Vistas | Thymeleaf |
| 🎨 Interfaz | Bootstrap 5, Font Awesome y jQuery mediante WebJars |
| 🗃️ Persistencia | Spring Data JPA + Hibernate |
| 🐬 Base de datos | MySQL |
| ✅ Validaciones | Jakarta Validation |
| 🔐 Seguridad | Spring Security + BCrypt |
| ✉️ Correo | Spring Mail |
| ☁️ Archivos e imágenes | Firebase Storage |
| 📊 Reportes Excel | Apache POI |
| 📄 Reportes PDF | OpenPDF |
| 🔳 Código QR | ZXing |
| 📦 Construcción | Maven Wrapper |
| 🔀 Control de versiones | Git + GitHub |

---

## 🔬 Funcionalidad investigada

Como funcionalidad adicional investigada se incorporó la generación de **códigos QR mediante ZXing**.

Cada herramienta puede disponer de un QR que dirige a una ficha pública dentro de TelecomTrack. La información mostrada se consulta dinámicamente desde la base de datos e incluye datos de identificación, estado y trazabilidad reciente.

> [!TIP]
> La explicación técnica y la justificación de esta funcionalidad se encuentran en [`docs/qr-functionalidad-investigada.md`](docs/qr-functionalidad-investigada.md).

---

## 🚀 Requisitos para ejecutar el proyecto

| Requisito | Uso |
|---|---|
| **JDK 21** | Ejecución del proyecto Java |
| **MySQL** | Persistencia de datos |
| **Maven Wrapper** | Compilación y ejecución |
| **Navegador web moderno** | Acceso a la aplicación |
| **Firebase Storage** | Evidencias fotográficas de devoluciones |
| **Cuenta SMTP** | Prueba de notificaciones reales por correo |

---

## ⚙️ Configuración local

El archivo real de configuración es:

```text
src/main/resources/application.properties
```

Este archivo **no se versiona en GitHub**. El repositorio incluye el archivo de ejemplo:

```text
src/main/resources/application.properties.example
```

### 🧭 Pasos de configuración

1. Copiar `application.properties.example`.
2. Renombrar la copia como `application.properties`.
3. Configurar la conexión a MySQL con los datos correspondientes al entorno local.

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://HOST:PORT/telecomtrack?sslMode=REQUIRED
spring.datasource.username=USUARIO_BASE_DATOS
spring.datasource.password=CONTRASENA_BASE_DATOS
```

> [!IMPORTANT]
> **Nota de configuración:** `application.properties` se mantiene únicamente en el entorno local para que cada integrante pueda utilizar sus propias credenciales y configuraciones sin publicarlas en el repositorio.

---

## 🗄️ Creación y carga de la base de datos

Los scripts se encuentran en:

```text
src/main/resources/sql/
├── schema/
└── seed/
```

### 1️⃣ Crear la estructura

Ejecutar **todos los archivos presentes en `sql/schema/` ordenados por nombre**, desde:

```text
01_create_usuario.sql
```

hasta:

```text
17_alter_herramienta_valor_unitario.sql
```

### 2️⃣ Cargar los datos de demostración

Ejecutar después **todos los archivos presentes en `sql/seed/` ordenados por nombre**.

> [!NOTE]
> Los scripts incluyen datos del dominio, usuarios de demostración, roles, rutas de autorización y configuraciones requeridas por los módulos finales.

---

## 🔐 Usuarios de prueba

Todos los usuarios de demostración utilizan la contraseña:

```text
Telecom123*
```

En la base de datos la contraseña se almacena mediante **BCrypt**.

| Rol | Correo | Estado |
|---|---|---|
| 👑 Administrador | `ana.rodriguez@telecomtrack.com` | ✅ Activo |
| 📦 Bodeguero | `carlos.mora@telecomtrack.com` | ✅ Activo |
| 🔧 Técnico | `luis.vargas@telecomtrack.com` | ✅ Activo |
| 📊 Supervisor | `maria.fernandez@telecomtrack.com` | ✅ Activo |
| 👁️ Visitante | `visitante@telecomtrack.com` | ✅ Activo |
| 🧪 Técnico de prueba | `jose.castro@telecomtrack.com` | ⛔ Inactivo |

> [!TIP]
> La documentación detallada se encuentra en [`docs/seguridad/usuarios-prueba.md`](docs/seguridad/usuarios-prueba.md).

---

## 👥 Acceso por rol

| Rol | Acceso principal |
|---|---|
| 👑 **Administrador** | Administración de usuarios, configuración y acceso general a los módulos del sistema. |
| 📦 **Bodeguero** | Inventario, movimientos, importación y atención de solicitudes. |
| 🔧 **Técnico** | Solicitudes propias, herramientas asignadas, devoluciones e historial personal. |
| 📊 **Supervisor** | Proyectos, listados estimados, dashboard y reportes asociados a sus responsabilidades. |
| 👁️ **Visitante** | Acceso autenticado básico y consulta de funcionalidades permitidas. |

> [!NOTE]
> El catálogo público de herramientas puede consultarse sin iniciar sesión.

---

## 📦 Importación CSV

La aplicación permite importar herramientas y materiales desde:

```text
/inventario/importar
```

La importación valida el encabezado, tipos de datos, duplicados y referencias relacionadas. Los errores se presentan indicando la fila correspondiente sin impedir que las filas válidas sean procesadas.

### 📚 Archivos de apoyo

| Recurso | Archivo |
|---|---|
| Documentación | [`docs/importacion/inventario-csv.md`](docs/importacion/inventario-csv.md) |
| Ejemplo de herramientas | [`docs/importacion/ejemplo_herramientas.csv`](docs/importacion/ejemplo_herramientas.csv) |
| Ejemplo de materiales | [`docs/importacion/ejemplo_materiales.csv`](docs/importacion/ejemplo_materiales.csv) |

---

## 🔥 Firebase Storage

Las fotografías asociadas a devoluciones se almacenan en **Firebase Storage**. MySQL conserva únicamente la URL de cada evidencia.

### Variables utilizadas

```text
FIREBASE_ENABLED=true
FIREBASE_BUCKET_NAME=<bucket>
FIREBASE_STORAGE_PATH=telecomtrack
FIREBASE_JSON_PATH=firebase
FIREBASE_JSON_FILE=<archivo-json-privado>
```

El archivo JSON privado se utiliza únicamente en el entorno local dentro de:

```text
src/main/resources/firebase/
```

> [!NOTE]
> La carpeta `src/main/resources/firebase/` se encuentra ignorada por Git.

Más información: [`docs/firebase-devoluciones.md`](docs/firebase-devoluciones.md)

---

## 📧 Spring Mail

Las notificaciones por correo utilizan las siguientes variables de entorno:

```text
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
```

> [!NOTE]
> Si no se configuran credenciales SMTP, el resto de las operaciones del sistema puede continuar funcionando sin depender del envío del correo.

---

## ▶️ Compilación y ejecución

### 🪟 Windows

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

### 🐧 Linux / macOS

```bash
./mvnw clean package
./mvnw spring-boot:run
```

### 🌍 Dirección local

```text
http://localhost
```

> [!TIP]
> El puerto puede modificarse desde `application.properties`.

---

## 🌐 Internacionalización

TelecomTrack utiliza archivos de mensajes de Thymeleaf para los siguientes idiomas:

| Idioma | Archivo |
|---|---|
| Predeterminado | `messages.properties` |
| 🇨🇷 Español | `messages_es.properties` |
| 🇺🇸 Inglés | `messages_en.properties` |
| 🇯🇵 Japonés | `messages_ja.properties` |
| 🇨🇳 Chino | `messages_zh.properties` |

La internacionalización se aplica a navegación, formularios, acciones, mensajes y módulos principales de la aplicación.

---

## 🗂️ Estructura principal

```text
telecomtrack/
├── docs/
├── src/
│   └── main/
│       ├── java/com/telecomtrack/
│       │   ├── controller/
│       │   ├── domain/
│       │   ├── dto/
│       │   ├── repository/
│       │   └── service/
│       └── resources/
│           ├── sql/
│           │   ├── schema/
│           │   └── seed/
│           ├── templates/
│           ├── static/
│           └── messages*.properties
├── pom.xml
├── mvnw
├── mvnw.cmd
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── README.md
```

### 🔄 Flujo MVC utilizado

```text
Entity → Repository → Service → Controller → Thymeleaf
```

> [!NOTE]
> La aplicación mantiene la separación de responsabilidades utilizada durante el curso.

---

## 👨‍💻 Equipo

| Integrante | GitHub |
|---|---|
| **Alberto Manuel Zúñiga Sánchez** | [@BetoxPrograming](https://github.com/BetoxPrograming) |
| **Allan Fauricio Fonseca Batista** | [@fauricio9656](https://github.com/fauricio9656) |

---

## 🔀 Repositorio y trazabilidad

El desarrollo se realizó mediante **Git y GitHub**, utilizando ramas de trabajo, commits y pull requests para integrar las funcionalidades del proyecto.

| Rama | Propósito |
|---|---|
| `main` | ✅ Versión estable para entrega |
| `develop` | 🔄 Rama de integración utilizada durante el desarrollo |
| `feature/*` | ✨ Funcionalidades asociadas a historias/issues |
| `fix/*` | 🐛 Correcciones y ajustes finales |

> [!NOTE]
> El historial del repositorio conserva la evidencia de integración y evolución progresiva del proyecto.

---

## 📚 Documentación adicional

| Documento | Ubicación |
|---|---|
| 📘 Avance 1 | [`docs/avance1/`](docs/avance1/) |
| 🔐 Usuarios de prueba | [`docs/seguridad/usuarios-prueba.md`](docs/seguridad/usuarios-prueba.md) |
| 📦 Importación CSV | [`docs/importacion/inventario-csv.md`](docs/importacion/inventario-csv.md) |
| 🔥 Firebase | [`docs/firebase-devoluciones.md`](docs/firebase-devoluciones.md) |
| 🔳 Funcionalidad QR investigada | [`docs/qr-functionalidad-investigada.md`](docs/qr-functionalidad-investigada.md) |

---

## 📌 Entrega final

La versión de `main` corresponde a la versión estable destinada a la **entrega final y defensa del proyecto de SC-403**.

### ✅ Verificación recomendada antes de la demostración

- [ ] Inicio de sesión con cada rol.
- [ ] Restricciones de acceso.
- [ ] Operaciones principales de inventario.
- [ ] Solicitudes y devoluciones.
- [ ] Dashboard y reportes.
- [ ] Internacionalización.
- [ ] Generación de QR.
- [ ] Firebase Storage cuando se utilicen fotografías.
- [ ] Notificaciones por correo cuando existan credenciales SMTP configuradas.

> [!IMPORTANT]
> Antes de la defensa se recomienda realizar una ejecución completa del sistema utilizando los usuarios de prueba y verificar los principales flujos transaccionales.
