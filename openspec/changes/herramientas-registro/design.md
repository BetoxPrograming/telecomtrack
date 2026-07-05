## Overview

This change follows the same layered Spring MVC structure already used by `Usuario` and `Ubicacion`: domain entity, repository, service, controller, and Thymeleaf views.

## Architecture

- `Herramienta` will be a JPA entity mapped to a database table.
- `HerramientaRepository` will extend `JpaRepository` and provide persistence access.
- `HerramientaService` will contain transactional read and write operations.
- `HerramientaController` will handle list, create, edit, and consult routes.
- Thymeleaf templates will provide the list and form screens.

## Validation and Data Rules

- Tool code must be unique.
- Form data must use the same validation style already used in the project.
- The tool record must be persisted in the database.

## UI Flow

```text
Listado -> Nuevo/Modificar -> Guardar -> Listado
      \-> Consultar
```

## Notes

- Keep the implementation aligned with the class material already present in the repo.
- Avoid introducing extra abstractions unless they are needed for the basic CRUD flow.
