## Why

The project needs a public read-only catalog so anyone can consult tool availability without editing data. This keeps the catalog separate from the administrative tool management flow.

## What Changes

- Add a public catalog of tools.
- Show only the available tools in the public catalog.
- Keep the catalog read-only with no create, edit, or delete actions.
- Reuse the same stored tool data instead of duplicating information.

## Capabilities

### New Capabilities
- `catalogo-publico-herramientas`: expose a read-only public list of available tools.

### Modified Capabilities

## Impact

- A read-only controller route and view are needed for the public catalog.
- The tool service must support a public availability filter.
- The catalog must not expose modification actions.
