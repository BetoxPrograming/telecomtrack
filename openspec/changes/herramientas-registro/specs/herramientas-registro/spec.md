## ADDED Requirements

### Requirement: Register tools
The system MUST allow registering a tool and storing it in the database.

#### Scenario: Tool registration succeeds
- **WHEN** the user submits a valid tool form
- **THEN** the system MUST save the tool and return to the tool list

### Requirement: Unique tool code
The system MUST reject a tool registration when the tool code already exists.

#### Scenario: Duplicate code is rejected
- **WHEN** the user submits a tool with a code already stored
- **THEN** the system MUST show a validation error and not save the tool

### Requirement: Consult tools
The system MUST allow consulting a stored tool from the tool list.

#### Scenario: Tool consultation succeeds
- **WHEN** the user selects a stored tool
- **THEN** the system MUST display the tool details
