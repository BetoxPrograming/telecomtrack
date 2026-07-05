## ADDED Requirements

### Requirement: Enter maintenance
The system MUST allow moving a tool into maintenance.

#### Scenario: Tool enters maintenance
- **WHEN** the user submits a valid maintenance request
- **THEN** the system MUST mark the tool as in maintenance

### Requirement: Estimated return date
The system MUST store an estimated return date when a tool enters maintenance.

#### Scenario: Estimated return date is saved
- **WHEN** the tool is moved into maintenance with a valid date
- **THEN** the system MUST store the estimated return date

### Requirement: Return to available
The system MUST allow a tool in maintenance to return to the available state.

#### Scenario: Tool returns to available
- **WHEN** the user confirms the return from maintenance
- **THEN** the system MUST set the tool state back to available
