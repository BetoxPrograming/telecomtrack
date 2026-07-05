## ADDED Requirements

### Requirement: Public available catalog
The system MUST expose a public catalog of available tools.

#### Scenario: Public catalog is displayed
- **WHEN** a user opens the public catalog route
- **THEN** the system MUST show the available tools

### Requirement: Read-only catalog
The public catalog MUST not allow create, edit, or delete actions.

#### Scenario: No modification actions are shown
- **WHEN** a user views the public catalog
- **THEN** the system MUST not display tool modification controls

### Requirement: Availability filter
The public catalog MUST only include tools that are available.

#### Scenario: Unavailable tools are hidden
- **WHEN** a tool is not available
- **THEN** the system MUST exclude it from the public catalog
