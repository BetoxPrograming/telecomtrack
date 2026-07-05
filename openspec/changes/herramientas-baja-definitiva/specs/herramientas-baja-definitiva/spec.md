## ADDED Requirements

### Requirement: Decommission tools
The system MUST allow marking a tool as permanently decommissioned without deleting its database record.

#### Scenario: Tool decommission succeeds
- **WHEN** the user submits a valid decommission request
- **THEN** the system MUST mark the tool as decommissioned and keep the record stored

### Requirement: Decommission justification
The system MUST require a justification before decommissioning a tool.

#### Scenario: Missing justification is rejected
- **WHEN** the user tries to decommission a tool without a justification
- **THEN** the system MUST reject the request and keep the tool active

### Requirement: Decommission date
The system MUST store the date when the tool is decommissioned.

#### Scenario: Decommission date is recorded
- **WHEN** the tool is successfully decommissioned
- **THEN** the system MUST save the corresponding decommission date
