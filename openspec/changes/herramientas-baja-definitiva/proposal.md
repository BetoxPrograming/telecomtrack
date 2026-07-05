## Why

The system needs a controlled way to retire tools permanently without deleting their history from the database. This keeps the inventory auditable and supports the basic decommission flow requested for the current advance.

## What Changes

- Add a definitive decommission action for tools.
- Keep decommissioned tools stored in the database.
- Record the decommission date and justification.
- Allow the decommission flow without role-based restrictions in this advance.

## Capabilities

### New Capabilities
- `herramientas-baja-definitiva`: mark a tool as permanently decommissioned while retaining its record.

### Modified Capabilities

## Impact

- Tool service and controller need a decommission action.
- Tool data model needs fields for decommission date and justification.
- List and consultation views need an action entry point for decommissioning.
