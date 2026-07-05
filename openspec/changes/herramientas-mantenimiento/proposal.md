## Why

Tools need a maintenance state so the team can track when a tool is temporarily unavailable and when it is expected to return. This advance covers the partial maintenance flow required by the issue.

## What Changes

- Add a maintenance action for tools.
- Record the estimated return date when a tool enters maintenance.
- Allow returning a tool from maintenance to available.
- Keep the maintenance flow partial and do not add assignment-blocking logic.

## Capabilities

### New Capabilities
- `herramientas-mantenimiento`: move tools into maintenance and back to available with an estimated return date.

### Modified Capabilities

## Impact

- Tool state handling must support maintenance transitions.
- Tool form or action screens must capture the estimated return date.
- List and consultation views must reflect maintenance status.
