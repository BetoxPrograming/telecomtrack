## Overview

This change adds a state transition from active tool to definitive decommission while preserving the record in the database.

## Architecture

- Reuse the same `Herramienta` entity and persistence layer used for the catalog.
- Add a decommission service method that updates the tool state and audit fields.
- Expose the action through the existing controller flow.
- Keep the UI simple: a form or action button that sends the justification.

## Rules

- The tool must not be deleted.
- The justification is mandatory.
- The decommission date must be written when the action succeeds.
- No role restriction is applied in this advance.

## UI Flow

```text
Listado/Consulta -> Baja definitiva -> Guardar -> Listado
```
