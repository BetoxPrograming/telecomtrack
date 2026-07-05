## Why

The project needs a formal tool registration module so tools can be stored, edited, and consulted from a single source of truth. This is the base needed before maintenance, decommission, and public catalog flows can be layered on top.

## What Changes

- Add a `Herramienta` capability for creating, listing, editing, and consulting tools.
- Validate that the tool code is unique before saving.
- Persist tool data in the database instead of keeping it only in memory or the UI.
- Add Thymeleaf screens for the tool list and the create/edit form.

## Capabilities

### New Capabilities
- `herramientas-registro`: register, list, edit, and consult tools with unique code validation.

### Modified Capabilities

## Impact

- New JPA entity, repository, service, and controller for tools.
- New Thymeleaf views and validation messages.
- Database schema and seed data for tool storage.
