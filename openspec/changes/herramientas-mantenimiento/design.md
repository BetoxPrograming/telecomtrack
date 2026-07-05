## Overview

This change adds a temporary state transition for tools so maintenance can be tracked without losing the tool record.

## Architecture

- Reuse the same tool entity and repository.
- Add service methods for entering maintenance and returning to available.
- Expose controller actions for both transitions.
- Capture the estimated return date in the maintenance form.

## Rules

- Maintenance is temporary.
- The estimated return date is required when moving into maintenance.
- A tool in maintenance can return to available.
- Assignment blocking is intentionally out of scope for this advance.

## UI Flow

```text
Listado/Consulta -> Mantenimiento -> Guardar -> Listado
Listado/Consulta -> Volver a disponible -> Guardar -> Listado
```
