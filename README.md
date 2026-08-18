# 📡 TelecomTrack

Sistema web transaccional para gestionar herramientas y materiales de **Telecom Power Systems**, mejorar la trazabilidad del inventario y registrar las operaciones asociadas a proyectos y personal técnico.

> [!IMPORTANT]
> TelecomTrack es el proyecto final del curso **SC-403 Desarrollo de Aplicaciones Web y Patrones** de la Universidad Fidélitas.
> La implementación debe respetar las tecnologías, dependencias, estructura MVC y forma de codificación trabajadas durante el curso. Un cambio fuera de esos parámetros puede invalidar el avance correspondiente.

---

## 📋 Descripción

Telecom Power Systems administraba herramientas y materiales mediante hojas de cálculo y registros manuales. Esto dificultaba conocer las existencias reales, identificar responsables, controlar las asignaciones y consultar el historial de movimientos.

TelecomTrack centraliza ese proceso mediante una aplicación web con inventario, solicitudes, aprobaciones, devoluciones, proyectos, usuarios, roles, dashboards en tiempo real, reportes exportables y registros transaccionales.

---

## ✅ Funcionalidades implementadas

| Módulo | Descripción |
|---|---|
| Usuarios y roles | Alta, edición y desactivación de usuarios (Administrador, Bodeguero, Técnico, Supervisor). |
| Ubicaciones | Bodegas/sitios donde reside el inventario. |
| Herramientas | Catálogo, estados (disponible, asignada, mantenimiento, baja) y ciclo de vida completo. |
| Materiales y stock | CRUD, entradas de stock, historial de movimientos y alerta de stock mínimo. |
| Proyectos | Registro de proyectos con supervisor asignado. |
| Listados de materiales estimados | Creación por proyecto y decisión del supervisor (aprobar, rechazar, pedir modificación). |
| Solicitudes | Solicitud de herramientas/materiales, aprobación o rechazo por bodega. |
| Dashboard | Indicadores en tiempo real para administrador y vista filtrada por proyectos para el supervisor. |
| Reportes | Consumo por proyecto, inventario general y activos por técnico, cada uno exportable a **Excel** y **PDF**. |
| Internacionalización | Español, inglés, japonés y chino mediante `MessageSource`. |

> [!NOTE]
> Autenticación real con Spring Security (login, roles aplicados vía `securityFilterChain`) y el módulo de devoluciones/trazabilidad todavía están en integración — ver issues abiertos del milestone vigente.

---

## 🛠️ Tecnologías

| Área | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4 |
| Arquitectura | MVC (`domain` → `repository` → `service` → `controller`) |
| Vistas | Thymeleaf + fragmentos reutilizables |
| Interfaz | Bootstrap 5 y Font Awesome vía WebJars |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL (esquema y datos por scripts SQL versionados, sin `ddl-auto`) |
| Reportes | Apache POI (Excel) y OpenPDF (PDF) |
| Construcción | Maven Wrapper (`mvnw` / `mvnw.cmd`) |
| Control de versiones | Git y GitHub |

---

## 🚀 Cómo ejecutar el proyecto

**Requisitos:** JDK 21 y acceso a una base de datos MySQL.

1. Configurar la conexión en `src/main/resources/application.properties` (`spring.datasource.url`, `username`, `password`).
2. Crear el esquema ejecutando, en orden, los scripts de `src/main/resources/sql/schema/` (numerados: `01_...` a `12_...`).
3. Cargar datos de ejemplo ejecutando, en el mismo orden, los scripts de `src/main/resources/sql/seed/`.
4. Compilar y ejecutar:

   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux/macOS
   ./mvnw spring-boot:run
   ```

5. La aplicación queda disponible en `http://localhost` (puerto 80, configurable en `application.properties`).

---

## 🗂️ Estructura del Repositorio

```text
telecomtrack/
├── docs/
│   └── avance1/
├── src/
│   ├── main/java/com/telecomtrack/
│   │   ├── controller/
│   │   ├── domain/
│   │   ├── dto/
│   │   ├── repository/
│   │   └── service/
│   └── main/resources/
│       ├── sql/schema/   # scripts DDL numerados
│       ├── sql/seed/     # datos de ejemplo numerados
│       ├── templates/    # vistas Thymeleaf por módulo
│       └── messages*.properties
├── .gitignore
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
└── README.md
```

---

## 👥 Equipo

Las responsabilidades se asignan y actualizan en cada avance según el backlog aprobado por el equipo.

| Integrante | GitHub |
|---|---|
| Allan Fauricio Fonseca Batista | [@fauricio9656](https://github.com/fauricio9656) |
| Carlos Roberto Pérez Rodríguez | [@ZerepSolrac412](https://github.com/ZerepSolrac412) |
| Sebastián Segura Camacho | [@SebastianSC11](https://github.com/SebastianSC11) |
| Alberto Manuel Zúñiga Sánchez | [@BetoxPrograming](https://github.com/BetoxPrograming) |

---

## 🔀 Flujo de Desarrollo

La rama `main` contiene únicamente versiones estables y entregables. La rama `develop` se utiliza para integrar y probar el trabajo del equipo antes de actualizar `main`. Cada rama de trabajo (`feature/*`, `fix/*`) se crea desde la versión más reciente de `develop`.

Al completar un issue:

1. El responsable publica su rama.
2. Abre un pull request hacia `develop`, relacionándolo con `Closes #<número>`.
3. Documenta las pruebas realizadas y cualquier limitación conocida.
4. Solicita la revisión correspondiente y atiende observaciones antes de integrar.

Los pull requests creados por otros integrantes requieren la aprobación de **@BetoxPrograming**, administrador del flujo de integración. Los pull requests creados por **@BetoxPrograming** requieren la aprobación de al menos otro integrante.

---

## 🤝 Contribución

El desarrollo directo corresponde a los integrantes del equipo y debe mantenerse dentro del alcance definido por el curso.

Antes de realizar cambios, es obligatorio leer:

- [Guía de contribución](CONTRIBUTING.md)
- [Código de conducta](CODE_OF_CONDUCT.md)

El incumplimiento de las normas académicas, técnicas o de colaboración puede poner en riesgo la entrega y será comunicado al docente cuando corresponda.

---

## 🙏 Reconocimientos

Este README fue redactado con apoyo de **Claude (Anthropic)**.
