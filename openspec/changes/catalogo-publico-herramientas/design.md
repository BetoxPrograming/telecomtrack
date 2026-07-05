## Overview

This change introduces a read-only catalog route over the existing tool data, filtered to available tools.

## Architecture

- Reuse the tool entity and repository.
- Add a service query for available tools.
- Add a public controller route that only renders the catalog view.
- Build a simple Thymeleaf view with no edit actions.

## Rules

- Only available tools are listed.
- No modification controls are shown.
- The public catalog must not create or update data.

## UI Flow

```text
Public home -> Catálogo -> Consultar disponibilidad
```
