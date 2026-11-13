# Requirements index

This app is built from two documents, both produced during planning and kept
here as the source of truth for scope and design intent:

- **[Field-Officer-Mobile-App-Plan.pdf](./Field-Officer-Mobile-App-Plan.pdf)**
  — the full plan: recommended native-Kotlin approach, current backend
  grounding (Spring Boot, auth, roles, M-Pesa STK push, storage), Phase 1-3
  feature scope, daily route planning, GPS tracking to the web admin,
  targets dashboard, backend/data-model additions table, phased timeline,
  risks and open decisions. §4.1 specifically documents the M-Pesa collection
  flow: the officer initiates the STK push, the customer enters their PIN on
  their own phone — never the officer's device.

- **[Field-Officer-App-Screen-Designs.pdf](./Field-Officer-App-Screen-Designs.pdf)**
  — 10 annotated phone-frame screen concepts (Login, Home, Customer List,
  Customer 360, Onboarding ID Capture, Loan Application, Collections/STK push
  3-state sequence, Daily Route, Visit Outcome, Targets Detail), using the
  "Copper Harvest" palette implemented in `core/designsystem/Color.kt`.

## Status vs. these documents

See the root [README.md](../README.md) "Build status" section for what's
implemented so far versus what's still planned.

## Backend dependency

This app calls `springboot-backend` from the main `jasiri` repository
(`D:\projects\jasiri`). It is **not** a standalone backend — see the plan's
§4 and §10 for the specific backend changes this app depends on (mobile
token response, `FIELD_OFFICER` role, idempotency keys, officer dashboard
endpoint, etc.). Several of those do not exist on the backend yet; this
project's TODO comments point back to the exact section that describes each
one.
